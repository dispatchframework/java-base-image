///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

/**
 * Main entry point for starting the function handling server
 */
public class Entrypoint {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        boolean springInClasspath = true;
        try {
            Class.forName("org.springframework.beans.factory.BeanFactory");
        } catch (Exception e) {
            springInClasspath = false;
        }

        Server server;
        try {
            if (springInClasspath) {
                // run spring function server
                server = new SpringFunctionServer(args);
            } else {
                // run plain old java function server
                server = new POJFunctionServer(args[0], args[1]);
            }
        } catch (Exception ex) {
            server = new ErrorServer(ex, springInClasspath);
        }

        try {
            server.start();
        } catch (Exception ex) {
            server.stop();

            // Don't create another ErrorServer if ErrorServer.start fails
            if (server instanceof ErrorServer) {
                throw ex;
            } else {
                server = new ErrorServer(ex, springInClasspath);
                server.start();
            }
        }

        synchronized (server) {
            server.wait();
        }

        server.stop();
    }

}
