///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Implementation of the FunctionExecutor. This class uses gson to
 * serialize/deserialize Dispatch function execution requests
 */
public class SimpleFunctionExecutor implements FunctionExecutor {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    private BiFunction f;
    private Type[] biFunctionTypes;

    public SimpleFunctionExecutor(BiFunction f) {
        this.f = f;

        this.biFunctionTypes = getBiFunctionTypes(f.getClass());
        if (biFunctionTypes == null || biFunctionTypes.length != 3) {
            throw new IllegalArgumentException(
                    String.format("%s does not implement the BiFunction interface", f.getClass().getName()));
        }
    }

    @Override
    public String execute(String message) {
        Request req = null;
        Object r = null;
        Error err = null;
        String jsonResponse;

        // Closing a ByteArrayOutputStream has no effect
        ByteArrayOutputStream baosStderr = new ByteArrayOutputStream();
        ByteArrayOutputStream baosStdout = new ByteArrayOutputStream();

        PrintStream oldStderr = System.err;
        PrintStream oldStdout = System.out;

        try (PrintStream stderr = new PrintStream(baosStderr); PrintStream stdout = new PrintStream(baosStdout)) {
            try {
                System.setErr(stderr);
                System.setOut(stdout);

                req = getRequest(message);
            } catch (Exception ex) {
                // If misaligned json type to BiFunction type
                if (ex.getCause() instanceof IllegalStateException) {
                    err = new Error(ex, ErrorType.INPUT_ERROR);
                } else {
                    err = new Error(ex, ErrorType.SYSTEM_ERROR);
                }
                ex.printStackTrace();
            }

            if (err == null) {
                Map<Object, Object> context = (Map<Object, Object>) req.getContext();
                Object payload = req.getPayload();
                Double timeout = (Double) context.getOrDefault("timeout", 0);
                Callable<Object> callable = () -> {
                    return f.apply(context, payload);
                };

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<Object> future = executorService.submit(callable);
                try {
                    if (timeout == 0) {
                        r = future.get();
                    } else {
                        r = future.get(new Double(timeout).longValue(), TimeUnit.MILLISECONDS);
                    }
                } catch (NumberFormatException e) {
                    err = new Error(e, ErrorType.INPUT_ERROR);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    err = new Error(e, ErrorType.SYSTEM_ERROR);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof IllegalArgumentException) {
                        err = new Error(e, ErrorType.INPUT_ERROR);
                    } else {
                        err = new Error(e, ErrorType.FUNCTION_ERROR);
                    }
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    err = new Error(e, ErrorType.SYSTEM_ERROR);
                    Entrypoint.healthy = false;
                    e.printStackTrace();
                } finally {
                    future.cancel(true);
                }
            }
        } finally {
            System.err.flush();
            System.setErr(oldStderr);

            System.out.flush();
            System.setOut(oldStdout);

            String[] stdoutLogs = baosStdout.toString().length() > 0 ? baosStdout.toString().split("\\r?\\n")
                    : new String[0];
            String[] stderrLogs = baosStderr.toString().length() > 0 ? baosStderr.toString().split("\\r?\\n")
                    : new String[0];
            Response response = new Response(new Context(err, new Logs(stderrLogs, stdoutLogs)), r);

            jsonResponse = gson.toJson(response);
        }

        return jsonResponse;
    }

    private Request getRequest(String message) {
        JsonObject rootObj = new JsonParser().parse(message).getAsJsonObject();
        JsonElement context = rootObj.get("context");
        JsonElement payload = rootObj.get("payload");

        return new Request(gson.fromJson(context, biFunctionTypes[0]), gson.fromJson(payload, biFunctionTypes[1]));
    }

    private Type[] getBiFunctionTypes(Class<?> functionClass) {
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

}
