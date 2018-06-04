///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Servlet used to respond to status/health requests of the server.
 */
public class HealthzServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        // No resources to initialize

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!Entrypoint.healthy) {
            throw new ServletException("Service not healthy.");
        }
        res.setContentType("application/json");
        try (Writer writer = res.getWriter()) {
            writer.append("{}");
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
        // No resources to release
    }

}
