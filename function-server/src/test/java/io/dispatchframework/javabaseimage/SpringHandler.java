///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringHandler {

    @Bean
    ExampleSpringFunction exampleSpringFunction() {
        return new ExampleSpringFunction();
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
