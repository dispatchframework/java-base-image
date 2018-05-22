///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import io.dispatchframework.javabaseimage.handlers.AbstractHandler;
import io.dispatchframework.javabaseimage.handlers.BadHandler;
import io.dispatchframework.javabaseimage.handlers.GoodHandler;
import io.dispatchframework.javabaseimage.handlers.PrivateHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpHandlersTest {
    private static final String packageName = "io.dispatchframework.javabaseimage.handlers";

    @Test
    public void getBiFunctionTypes_WithInterface() throws Exception {
        HttpHandlers.ExecFunction goodHandler = new HttpHandlers.ExecFunction(GoodHandler.class);
        assertNotNull(goodHandler.executor);
        assertNotNull(goodHandler.f);
    }

    @Test
    public void getBiFunctionTypes_WithoutInterface() {
        assertThrows(ClassCastException.class, () -> new HttpHandlers.ExecFunction(BadHandler.class));
    }

    @Test
    public void execFunction_WithAbstractBiFunction() {
        assertThrows(InstantiationException.class, () -> new HttpHandlers.ExecFunction(AbstractHandler.class));
    }

    @Test
    public void execFunction_WithPrivateBiFunctionConstructor() {
        assertThrows(IllegalAccessException.class, () -> new HttpHandlers.ExecFunction(PrivateHandler.class));
    }

}