///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A Server implementation for running Dispatch functions in a Spring
 * Application Context.
 */
public class SpringFunctionServer implements Server {

    private Class clazz;

    private AnnotationConfigApplicationContext ctx;

    public SpringFunctionServer(Class c) {
        this.clazz = c;
    }

    public void start() {
        ctx = new AnnotationConfigApplicationContext();
        ctx.register(DispatchSpringConfig.class, clazz);
        ctx.refresh();
    }

    public void stop() {
        ctx.close();
    }
}
