///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.handlers;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Configuration
public class UnannotatedSpringHandler {
    UnannotatedSpringHandler.ExampleSpringFunction exampleSpringFunction() {
        return new UnannotatedSpringHandler.ExampleSpringFunction();
    }

    private class ExampleSpringFunction implements BiFunction<Map<Object, Object>, Map<Object,Object>, Map<Object, Object>> {

        @Override
        public Map<Object, Object> apply(Map<Object, Object> context, Map<Object, Object> payload) {
            Map<Object, Object> result = new HashMap<Object, Object>();
            result.put("hello", "world");
            return result;
        }

    }
}
