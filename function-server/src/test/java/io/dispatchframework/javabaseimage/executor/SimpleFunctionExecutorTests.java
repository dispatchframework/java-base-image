///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import io.dispatchframework.javabaseimage.handlers.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
    public void test_execute_success() throws DispatchException {

        BiFunction<Map<Object, Object>, String, String> successFunction = new BiFunction<Map<Object, Object>, String, String>() {

            @Override
            public String apply(Map<Object, Object> context, String payload) {
                return "The content-type is: " + context.get("content-type") + ", with payload: " + payload;
            }

        };

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(successFunction, executorService);

        String expected = "\"The content-type is: application/json, with payload: test\"";
        String actual = principal.execute("{\"context\" : { \"content-type\" : \"application/json\", \"timeout\" : 0.0}, \"payload\" : \"test\"}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_empty() throws DispatchException {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);

        String expected = "\"Hello, Someone from Somewhere\"";
        String actual = principal.execute("{}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_logging() throws DispatchException {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(new Logger(), executorService);

        String expected = "\"\"";
        String actual = principal.execute("{\"context\": null, \"payload\": null}");
        assertEquals(expected, actual);
    }

    @Test
    public void test_execute_mismatchedPayload() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);

        String message = "{\"context\": null, \"payload\": \"invalid\"}";
        try {
            principal.execute(message);
        } catch (DispatchException e) {
            assertTrue(e.getError().contains(ErrorType.INPUT_ERROR.toString()));
        }
    }

    @Test
    public void test_execute_systemError() {
        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction, executorService);
        try {
            principal.execute("{");
        } catch (DispatchException e) {
            assertTrue(e.getError().contains(ErrorType.SYSTEM_ERROR.toString()));
        }
    }

    @Test
    public void test_execute_functionError() {
        BiFunction<Map<String, Object>, Map<String, Object>, String> failFunction = new Fail();

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(failFunction, executorService);

        try {
            principal.execute("{}");
        } catch (DispatchException e) {
            assertTrue(e.getError().contains(ErrorType.FUNCTION_ERROR.toString()));
        }
    }

    @Test
    public void test_execute_inputError() {
        BiFunction<Map<String, Object>, Map<String, Object>, String> lowerFunction = new Lower();

        SimpleFunctionExecutor principal = new SimpleFunctionExecutor(lowerFunction, executorService);

        try {
            principal.execute("{\"context\": null, \"payload\": {\"name\": 1}}");
        } catch (DispatchException e) {
            assertTrue(e.getError().contains(ErrorType.INPUT_ERROR.toString()));
        }
    }
}
