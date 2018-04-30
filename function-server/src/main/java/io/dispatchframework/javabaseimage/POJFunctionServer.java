package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.Undertow;
import io.undertow.util.Headers;

public class POJFunctionServer implements Server {
	private static final Gson gson = new GsonBuilder().serializeNulls().create();

    private Undertow undertow;
    public POJFunctionServer(String packageName, String className) throws Exception {
        undertow = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath("/", new ExecFunction(packageName, className))
                        .addExactPath("/healthz", new Healthz()))
                .build();
    }

    public void start() {
    	this.undertow.start();
    }

    public void stop() {
    	this.undertow.stop();
    }

    private static void receiverErrorCallback(HttpServerExchange exchange, IOException ex) {
        // Closing a StringWriter has no effect
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            ex.printStackTrace(pw);

            String[] stderrLogs = sw.toString().split("\\r?\\n");
            Response response = new Response(new Context(ex, new Logs(stderrLogs, new String[0])), null);

            String jsonResponse = gson.toJson(response);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        }
    }

    public static class ExecFunction implements HttpHandler {
    	final BiFunction f;
    	final FunctionExecutor executor;

        public ExecFunction(String packageName, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Class<?> functionClass = Class.forName(packageName + "." + className);
            this.f = (BiFunction) functionClass.newInstance();
            executor = new SimpleFunctionExecutor(f);
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
            	String response = executor.execute(message);
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(response);
            }, POJFunctionServer::receiverErrorCallback);
        }
    }

    public static class Healthz implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
                String jsonResponse = "{}";
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(jsonResponse);
            }, POJFunctionServer::receiverErrorCallback);
        }
    }
}