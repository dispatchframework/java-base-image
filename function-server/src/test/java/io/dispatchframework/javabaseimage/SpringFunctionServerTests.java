///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

public class SpringFunctionServerTests {

    private static final String packageName = "io.dispatchframework.javabaseimage";

    @Test
    public void test_start_unannotatedClass() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(new String[] {packageName, "BadHandler"});

        assertThrows(UnsatisfiedDependencyException.class, () -> server.start());
    }

    @Test
    public void test_start_annotatedClass() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(new String[] {packageName, "SpringHandler"});
        
        server.start();
    }

}
