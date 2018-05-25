///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import io.dispatchframework.javabaseimage.handlers.InvalidBean;
import io.dispatchframework.javabaseimage.handlers.PrivateSpringHandler;
import io.dispatchframework.javabaseimage.handlers.SpringHandler;
import io.dispatchframework.javabaseimage.handlers.UnannotatedSpringHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpringFunctionServerTests {
    @Test
    public void test_start_unannotatedClass() {
        SpringFunctionServer server = new SpringFunctionServer(UnannotatedSpringHandler.class);

        assertThrows(UnsatisfiedDependencyException.class, () -> server.start());
        server.stop();
    }

    @Test
    public void test_start_annotatedClass() {
        SpringFunctionServer server = new SpringFunctionServer(SpringHandler.class);

        server.start();
        server.stop();
    }

    @Test
    public void test_start_privateClassConstructor() {
        SpringFunctionServer server = new SpringFunctionServer(PrivateSpringHandler.class);

        assertThrows(IllegalStateException.class, () -> server.start());
        server.stop();
    }

    @Test
    public void test_start_invalidBean() {
        SpringFunctionServer server = new SpringFunctionServer(InvalidBean.class);

        assertThrows(BeanDefinitionParsingException.class, () -> server.start());
        server.stop();
    }

}
