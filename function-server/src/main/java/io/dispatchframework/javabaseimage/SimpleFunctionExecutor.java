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
    private ExecutorService executorService;

    public SimpleFunctionExecutor(BiFunction f, ExecutorService executorService) {
        this.f = f;
        this.executorService = executorService;

        this.biFunctionTypes = getBiFunctionTypes(f.getClass());
        if (biFunctionTypes == null || biFunctionTypes.length != 3) {
            throw new IllegalArgumentException(
                    String.format("%s does not implement the BiFunction interface", f.getClass().getName()));
        }
    }

    @Override
    public String execute(String message) throws DispatchException {
        Request req = null;
        Object r = null;
        Error err = null;
        String jsonResponse;

        try {
            try {
                req = getRequest(message);
            } catch (Exception ex) {
                // If misaligned json type to BiFunction type
                if (ex.getCause() instanceof IllegalStateException) {
                    err = new Error(ex, ErrorType.INPUT_ERROR);
                    throw new DispatchException(402, gson.toJson(err));
                } else {
                    err = new Error(ex, ErrorType.SYSTEM_ERROR);
                    throw new DispatchException(500, gson.toJson(err));
                }
            }

            if (err == null) {
                try {
                    r = f.apply(req.getContext(), req.getPayload());
                } catch (IllegalArgumentException e) {
                    err = new Error(e, ErrorType.INPUT_ERROR);
                    throw new DispatchException(422, gson.toJson(err));
                } catch (Exception e) {
                    err = new Error(e, ErrorType.FUNCTION_ERROR);
                    throw new DispatchException(502, gson.toJson(err));
                }
            }
        } finally {
            jsonResponse = gson.toJson(r);
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
