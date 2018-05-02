///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.util.Map;
import java.util.function.BiFunction;

public class Logger implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        System.out.println("stdout");
        System.out.println("stdout2");
        System.err.println("stderr");
        System.err.println("stderr2");

        return "";
    }
}
