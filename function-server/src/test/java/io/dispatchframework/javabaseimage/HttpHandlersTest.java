///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class HttpHandlersTest {
    private static final String packageName = "io.dispatchframework.javabaseimage.Handlers";

    @Test
    public void getBiFunctionTypes_WithInterface() throws Exception {
        HttpHandlers.ExecFunction goodHandler = new HttpHandlers.ExecFunction(packageName, "GoodHandler");
        assertNotNull(goodHandler.executor);
        assertNotNull(goodHandler.f);
    }

    @Test
    public void getBiFunctionTypes_WithoutInterface() {
        assertThrows(ClassCastException.class, () -> new HttpHandlers.ExecFunction(packageName, "BadHandler"));
    }

    @Test
    public void execFunction_WithAbstractBiFunction() {
        assertThrows(InstantiationException.class, () -> new HttpHandlers.ExecFunction(packageName, "AbstractHandler"));
    }

    @Test
    public void execFunction_WithPrivateBiFunctionConstructor() {
        assertThrows(IllegalAccessException.class, () -> new HttpHandlers.ExecFunction(packageName, "PrivateHandler"));
    }
}