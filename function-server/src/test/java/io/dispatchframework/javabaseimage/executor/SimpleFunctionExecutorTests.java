/* **********************************************************
 * SimpleFunctionExecutorTests.java
 *
 * Copyright (C) 2018 VMware, Inc.
 * All Rights Reserved
 * **********************************************************/
package io.dispatchframework.javabaseimage.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.dispatchframework.javabaseimage.SimpleFunctionExecutor;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
public class SimpleFunctionExecutorTests {

	private static BiFunction<Map<Object, Object>, Map<Object, Object>, String> helloFunction;
	
	@BeforeAll()
	private static void setup() {
		helloFunction = new BiFunction<Map<Object, Object>, Map<Object, Object>, String>() {

			@Override
			public String apply(Map<Object, Object> context, Map<Object, Object> payload) {
				if (payload == null) {
		            return "Hello, Someone from Somewhere";
		        }

		        final Object name = payload.getOrDefault("name", "Someone");
		        final Object place = payload.getOrDefault("place", "Somewhere");

		        return String.format("Hello, %s from %s", name, place);
			}
			
		};
	}

	@Test
	public void test_constructor_success() {
		BiFunction<String, String, String> principal = new BiFunction<String, String, String>() {

			@Override
			public String apply(String t, String u) {
				return "";
			}
			
		};

		SimpleFunctionExecutor executor = new SimpleFunctionExecutor(principal);

		assertNotNull(executor);
	}

	@Test()
	public void test_constructor_illegalBiFunction() {
		BiFunction principal = (a, b) -> "";

		assertThrows(IllegalArgumentException.class, () -> new SimpleFunctionExecutor(principal));
	}

	@Test
	public void test_execute_success() {

		BiFunction<Map<Object, Object>, String, String> successFunction = new BiFunction<Map<Object, Object>, String, String>() {

			@Override
			public String apply(Map<Object, Object> context, String payload) {
				return "The content-type is: " + context.get("content-type") + ", with payload: " + payload;
			}
			
		};

		SimpleFunctionExecutor principal = new SimpleFunctionExecutor(successFunction);

		String expected = "{\"context\":{\"error\":null,\"logs\":{\"stderr\":[],\"stdout\":[]}},\"payload\":\"The content-type is: application/json, with payload: test\"}";
		String actual = principal.execute("{\"context\" : { \"content-type\" : \"application/json\"}, \"payload\" : \"test\"}");
		assertEquals(expected, actual);
	}

	@Test()
	public void test_execute_empty() {
		SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction);

		String expected = "{\"context\":{\"error\":null,\"logs\":{\"stderr\":[],\"stdout\":[]}},\"payload\":\"Hello, Someone from Somewhere\"}";
		String actual = principal.execute("{}");
		assertEquals(expected, actual);
	}

	@Test
	public void test_execute_mismatchedPayload() {
		SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction);

		String message = "{\"context\": null, \"payload\": \"invalid\"}";
		String actual = principal.execute(message);
		assertTrue(actual.contains("error"));
	}

	@Test
	public void test_execute_invalidJson() {
		SimpleFunctionExecutor principal = new SimpleFunctionExecutor(helloFunction);
		String actual = principal.execute("{");
		assertTrue(actual.contains("error"));
	}
}
