///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import io.dispatchframework.javabaseimage.handlers.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import io.dispatchframework.javabaseimage.DispatchException;
import io.dispatchframework.javabaseimage.ErrorType;
import io.dispatchframework.javabaseimage.handlers.Fail;
import io.dispatchframework.javabaseimage.handlers.Hello;
import io.dispatchframework.javabaseimage.handlers.Lower;
import io.dispatchframework.javabaseimage.SimpleFunctionExecutor;

/**
 *
 */
public class SimpleFunctionExecutorTests {

    private static BiFunction<Map<String, Object>, Map<String, Object>, String> helloFunction;
    private static ExecutorService executorService;

    @BeforeAll
    private static void setup() {
        helloFunction = new Hello();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Test
    public void test_constructor_success() {
        BiFunction<String, String, String> principal = new BiFunction<String, String, String>() {

            @Override
            public String apply(String t, String u) {
                return "";
            }

        };

        SimpleFunctionExecutor executor = new SimpleFunctionExecutor(principal, executorService);

        assertNotNull(executor);
    }

    @Test
    public void test_constructor_illegalBiFunction() {
        BiFunction principal = (a, b) -> "";

        assertThrows(IllegalArgumentException.class, () -> new SimpleFunctionExecutor(principal, executorService));
    }

    @Test
    public void test_execute_success() {

        BiFunction<Map<Object, Object>, String, String> successFunction = new BiFunction<Map<Object, Object>, String, String>() {

            @Override
            public String apply(Map<Object, Object> context, String payload) {
                return "The content-type is: " + context.get("content-type") + ", with payload: " + payload;
            }

        };

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(successFunction, executorService);

        String expected = "{\"context\":{\"error\":null,\"logs\":{\"stderr\":[],\"stdout\":[]}},\"payload\":\"The content-type is: application/json, with payload: test\"}";
        String actual = principal.execute("{\"context\" : { \"content-type\" : \"application/json\", \"timeout\" : 0.0}, \"payload\" : \"test\"}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_empty() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);

        String expected = "{\"context\":{\"error\":null,\"logs\":{\"stderr\":[],\"stdout\":[]}},\"payload\":\"Hello, Someone from Somewhere\"}";
        String actual = principal.execute("{}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_logging() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(new Logger(), executorService);

        String expected = "{\"context\":{\"error\":null,\"logs\":{\"stderr\":[\"stderr\",\"stderr2\"],\"stdout\":[\"stdout\",\"stdout2\"]}},\"payload\":\"\"}";
        String actual = principal.execute("{\"context\": null, \"payload\": null}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_mismatchedPayload() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);

        String message = "{\"context\": null, \"payload\": \"invalid\"}";
        String actual = principal.execute(message);
        assertTrue(actual.contains(ErrorType.INPUT_ERROR.toString()));
    }

    @Test
    public void test_execute_systemError() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);
        String actual = principal.execute("{");

        assertTrue(actual.contains(ErrorType.SYSTEM_ERROR.toString()));
    }

    @Test
    public void test_execute_functionError() {
        BiFunction<Map<String, Object>, Map<String, Object>, String> failFunction = new Fail();

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(failFunction, executorService);

        String actual = principal.execute("{}");
        assertTrue(actual.contains(ErrorType.FUNCTION_ERROR.toString()));
    }

    @Test
    public void test_execute_inputError() {
        BiFunction<Map<String, Object>, Map<String, Object>, String> lowerFunction = new Lower();

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(lowerFunction, executorService);

        String actual = principal.execute("{\"context\": null, \"payload\": {\"name\": 1}}");
        assertTrue(actual.contains(ErrorType.INPUT_ERROR.toString()));
    }

    @Test
    public void test_withTimeout_noTimeout() throws DispatchException {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);

        HashMap<Object, Object> context = new HashMap<Object, Object>();
        context.put("timeout", 0.0);

        HashMap<Object, Object> payload = new HashMap<Object, Object>();
        payload.put("name", "Jon");
        payload.put("place", "Winterfell");

        Object actual = principal.withTimeout(helloFunction, context, payload);

        assertEquals("Hello, Jon from Winterfell", actual, "Did not receive expected function execution result.");
    }

    @Test
    public void test_withTimeout_TimeoutException() throws Exception {
        Future future = Mockito.mock(Future.class);
        when(future.get(eq(2000l), eq(TimeUnit.MILLISECONDS))).thenThrow(TimeoutException.class);

        ExecutorService execService = Mockito.mock(ExecutorService.class);
        when(execService.submit(any(Callable.class))).thenReturn(future);

        HashMap<Object, Object> context = new HashMap<Object, Object>();
        context.put("timeout", 2000.0);

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, execService);

        assertThrows(DispatchException.class, () -> principal.withTimeout(helloFunction, context, Collections.emptyMap()));
        verify(future, times(1)).get(eq(2000l), eq(TimeUnit.MILLISECONDS));
        verify(future, never()).get();
        verify(future, times(1)).cancel(eq(true));
    }

    @Test
    public void test_withTimeout_ExecutionException() throws Exception {
        Future future = Mockito.mock(Future.class);
        when(future.get()).thenThrow(ExecutionException.class);

        ExecutorService execService = Mockito.mock(ExecutorService.class);
        when(execService.submit(any(Callable.class))).thenReturn(future);

        HashMap<Object, Object> context = new HashMap<Object, Object>();
        context.put("timeout", 0.0);

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, execService);

        assertThrows(DispatchException.class, () -> principal.withTimeout(helloFunction, context, Collections.emptyMap()));
        verify(future, times(1)).get();
        verify(future, times(1)).cancel(eq(true));
    }
}
