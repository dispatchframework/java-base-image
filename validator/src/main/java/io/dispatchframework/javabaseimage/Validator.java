///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static io.dispatchframework.javabaseimage.Entrypoint.isSpringAnnotated;

/**
 * Validates user input function and specified handler. If there is an exception encountered
 * when trying to load the class, the exception will be thrown and the program will exit.
 */
public class Validator {
    public static void main(String[] args) throws Exception {
        Class c = Class.forName(args[0]);

        if (isSpringAnnotated(c)) {
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.register(SpringServletConfig.class, c);
            ctx.refresh();
            ctx.close();
        } else {
            new HttpHandlers.ExecFunction(c);
        }
    }
}
