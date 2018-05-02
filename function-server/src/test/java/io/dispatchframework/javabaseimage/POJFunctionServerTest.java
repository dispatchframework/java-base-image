///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class POJFunctionServerTest {
    private static final String packageName = "io.dispatchframework.javabaseimage";

    @Test
    public void getBiFunctionTypes_WithInterface() throws Exception {
        POJFunctionServer.ExecFunction goodHandler = new POJFunctionServer.ExecFunction(packageName, "GoodHandler");
        assertNotNull(goodHandler.executor);
        assertNotNull(goodHandler.f);
    }

    @Test
    public void getBiFunctionTypes_WithoutInterface() {
        assertThrows(ClassCastException.class, () -> new POJFunctionServer.ExecFunction(packageName, "BadHandler"));
    }
}