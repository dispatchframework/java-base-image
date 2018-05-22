///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;

/**
 * Contains http handlers for undertow server.
 */
public final class HttpHandlers {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * HttpHandler for executing functions with request params
     */
    public static class ExecFunction implements HttpHandler {
        final BiFunction f;
        final FunctionExecutor executor;

        public ExecFunction(Class handler)
                throws InstantiationException, IllegalAccessException {
            this.f = (BiFunction) handler.newInstance();
            executor = new SimpleFunctionExecutor(f);
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
                String response = executor.execute(message);
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(response);
            }, HttpHandlers::receiverErrorCallback);
        }
    }

    /**
     * HttpHandler for responding to health checks
     */
    public static class Healthz implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
                String jsonResponse = "{}";
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(jsonResponse);
            }, HttpHandlers::receiverErrorCallback);
        }
    }

    /**
     * HttpHandler for returning error response from POJFunctionServer
     */
    public static class POJErrorHandler implements HttpHandler {
        private Error err;

        public POJErrorHandler(Exception ex) {
            if (ex instanceof ClassNotFoundException ||
                ex instanceof InstantiationException ||
                ex instanceof IllegalAccessException ||
                ex instanceof IllegalArgumentException ||
                ex instanceof ClassCastException) {
                this.err = new Error(ex, ErrorType.INPUT_ERROR);
            } else {
                this.err = new Error(ex, ErrorType.SYSTEM_ERROR);
            }
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) ->
                            sendErrorResponse(httpServerExchange, err),
                    HttpHandlers::receiverErrorCallback);
        }
    }

    /**
     * HttpHandler for returning error response from SpringFunctionServer
     */
    public static class SpringErrorHandler implements HttpHandler {
        private Error err;

        public SpringErrorHandler(Exception ex) {
            if (ex instanceof ClassNotFoundException ||
                ex instanceof IllegalStateException ||
                ex instanceof IllegalArgumentException ||
                ex instanceof BeanDefinitionParsingException ||
                ex instanceof UnsatisfiedDependencyException) {
                this.err = new Error(ex, ErrorType.INPUT_ERROR);
            } else {
                this.err = new Error(ex, ErrorType.SYSTEM_ERROR);
            }
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) ->
                            sendErrorResponse(httpServerExchange, err),
                    HttpHandlers::receiverErrorCallback);
        }
    }

    private static void receiverErrorCallback(HttpServerExchange exchange, IOException ex) {
        Error err = new Error(ex, ErrorType.SYSTEM_ERROR);
        sendErrorResponse(exchange, err);
    }

    private static void sendErrorResponse(HttpServerExchange exchange, Error err) {
        Response response = new Response(new Context(err, new Logs(err.getStacktrace(), new String[0])), null);

        String jsonResponse = gson.toJson(response);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonResponse);
    }
}
