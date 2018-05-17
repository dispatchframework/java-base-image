///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.Handlers;

import java.util.function.BiFunction;

public abstract class AbstractHandler implements BiFunction<String, String, Integer> {
    @Override
    public Integer apply(String context, String payload)  {
        return 0;
    }
}
