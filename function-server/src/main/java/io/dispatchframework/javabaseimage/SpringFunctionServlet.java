///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Servlet implementation used to respond to Dispatch function execution
 * requests.
 */
public class SpringFunctionServlet implements Servlet {

    private FunctionExecutor executor;

    public SpringFunctionServlet(BiFunction f, ExecutorService executorService) {
        this.executor = new SimpleFunctionExecutor(f, executorService);
    }

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
        String response = executor.execute(getBody(req));

        res.setContentType("application/json");
        try (Writer writer = res.getWriter()) {
            writer.append(response);
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

    private String getBody(ServletRequest req) throws IOException {
        StringBuffer sb = new StringBuffer();

        try (Reader reader = req.getReader()) {
            char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = reader.read(charBuffer)) > 0) {
                sb.append(charBuffer, 0, bytesRead);
            }
        }

        return sb.toString();
    }
}
