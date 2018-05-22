///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.handlers;

import java.util.function.BiFunction;

public class PrivateHandler implements BiFunction<String, String, Integer> {
    private PrivateHandler() {}

    @Override
    public Integer apply(String context, String payload)  {
        return 0;
    }
}
