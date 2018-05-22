///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.handlers;

import java.util.Map;
import java.util.function.BiFunction;

public class Lower implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        final Object name = payload.getOrDefault("name", "SOMEONE");

        if (name instanceof String) {
            return ((String) name).toLowerCase();
        } else {
            throw new IllegalArgumentException("name is not of type string");
        }
    }
}
