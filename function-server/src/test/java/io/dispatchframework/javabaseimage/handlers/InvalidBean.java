///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiFunction;

@Configuration
public class InvalidBean {

    @Bean
    private InvalidBean.ExampleSpringFunction exampleSpringFunction() {
        return new InvalidBean.ExampleSpringFunction();
    }

    private class ExampleSpringFunction implements BiFunction<String, String, Integer> {

        @Override
        public Integer apply(String context, String payload) {
            return 0;
        }

    }
}
