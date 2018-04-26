package io.dispatchframework.javabaseimage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.Undertow;
import io.undertow.util.Headers;

public class Server {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static void main(String[] args) throws Exception {
        Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath("/", new ExecFunction(args[0], args[1]))
                        .addExactPath("/healthz", new Healthz()))
                .build()
                .start();
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
        final Type[] biFunctionTypes;
        final BiFunction f;

        public ExecFunction(String packageName, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Class<?> functionClass = Class.forName(packageName + "." + className);
            Type[] biFunctionTypes = getBiFunctionTypes(functionClass);
            if (biFunctionTypes == null || biFunctionTypes.length != 3) {
                throw new IllegalArgumentException(String.format("%s.%s does not implement the BiFunction interface", packageName, className));
            }
            this.biFunctionTypes = biFunctionTypes;
            this.f = (BiFunction) functionClass.newInstance();
        }

        public Type[] getBiFunctionTypes(Class<?> functionClass) {
            Type[] genericInterfaces = functionClass.getGenericInterfaces();
            Type[] genericTypes = null;

            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    if (parameterizedType.getRawType().equals(BiFunction.class)) {
                        genericTypes = parameterizedType.getActualTypeArguments();
                        break;
                    }
                }
            }

            return genericTypes;
        }

        public Object applyFunction(String message) {
            JsonObject rootObj = new JsonParser().parse(message).getAsJsonObject();
            JsonElement context = rootObj.get("context");
            JsonElement payload = rootObj.get("payload");

            return f.apply(gson.fromJson(context, biFunctionTypes[0]), gson.fromJson(payload, biFunctionTypes[1]));
        }

        public Response processMessage(String message) {
            Response response;
            Object r = null;
            Exception err = null;

            // Closing a ByteArrayOutputStream has no effect
            ByteArrayOutputStream baosStderr = new ByteArrayOutputStream();
            ByteArrayOutputStream baosStdout = new ByteArrayOutputStream();

            PrintStream oldStderr = System.err;
            PrintStream oldStdout = System.out;

            try (PrintStream stderr = new PrintStream(baosStderr);
                 PrintStream stdout = new PrintStream(baosStdout)) {
                try {
                    System.setErr(stderr);
                    System.setOut(stdout);

                    r = applyFunction(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    err = ex;
                } finally {
                    System.err.flush();
                    System.setErr(oldStderr);

                    System.out.flush();
                    System.setOut(oldStdout);

                    String[] stderrLogs = baosStderr.toString().length() > 0 ? baosStderr.toString().split("\\r?\\n") : new String[0];
                    String[] stdoutLogs = baosStdout.toString().length() > 0 ? baosStdout.toString().split("\\r?\\n") : new String[0];
                    response = new Response(new Context(err, new Logs(stderrLogs, stdoutLogs)), r);
                }
            }

            return response;
        }

        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
                Response response = processMessage(message);

                String jsonResponse = gson.toJson(response);
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(jsonResponse);
            }, Server::receiverErrorCallback);
        }
    }

    public static class Healthz implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) {
            exchange.getRequestReceiver().receiveFullString((httpServerExchange, message) -> {
                String jsonResponse = "{}";
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseSender().send(jsonResponse);
            }, Server::receiverErrorCallback);
        }
    }
}