///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import io.dispatchframework.javabaseimage.handlers.BadHandler;
import io.dispatchframework.javabaseimage.handlers.InvalidBean;
import io.dispatchframework.javabaseimage.handlers.PrivateSpringHandler;
import io.dispatchframework.javabaseimage.handlers.SpringHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpringFunctionServerTests {

    private static final String packageName = "io.dispatchframework.javabaseimage.handlers";

    @Test
    public void test_start_unannotatedClass() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(BadHandler.class);

        assertThrows(UnsatisfiedDependencyException.class, () -> server.start());
        server.stop();
    }

    @Test
    public void test_start_annotatedClass() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(SpringHandler.class);

        server.start();
        server.stop();
    }

    @Test
    public void test_start_privateClassConstructor() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(PrivateSpringHandler.class);

        assertThrows(IllegalStateException.class, () -> server.start());
        server.stop();
    }

    @Test
    public void test_start_invalidBean() throws ClassNotFoundException {
        SpringFunctionServer server = new SpringFunctionServer(InvalidBean.class);

        assertThrows(BeanDefinitionParsingException.class, () -> server.start());
        server.stop();
    }

}
