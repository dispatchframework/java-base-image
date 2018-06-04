///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.Configuration;

/**
 * Main entry point for starting the function handling server
 */
public class Entrypoint {

    public static boolean healthy = true;

    public static void main(String[] args) throws Exception {

        Class<?> c = Class.forName(args[0]);

        Server server = (isSpringAnnotated(c) ? new SpringFunctionServer(c) : new POJFunctionServer(c));

        try {
            System.err.printf("Starting Function Server for '%s'\n", args[0]);
            server.start();
            synchronized (server) {
                server.wait();
            }
        } finally {
            server.stop();
        }
    }

    public static boolean isSpringAnnotated(Class<?> c) {
        return c.getAnnotation(Configuration.class) != null;
    }

}
