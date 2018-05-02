///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.util.Map;
import java.util.function.BiFunction;

public class Hello implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        if (payload == null) {
            return "Hello, Someone from Somewhere";
        }

        final Object name = payload.getOrDefault("name", "Someone");
        final Object place = payload.getOrDefault("place", "Somewhere");

        return String.format("Hello, %s from %s", name, place);
    }
}
