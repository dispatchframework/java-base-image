///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.BiFunction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation used to respond to Dispatch function execution
 * requests.
 */
public class SpringFunctionServlet extends HttpServlet {

    private FunctionExecutor executor;

    public SpringFunctionServlet(BiFunction<?, ?, ?> f) {
        this.executor = new SimpleFunctionExecutor(f);
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
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doProcess(req, res);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doProcess(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doProcess(req, res);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doProcess(req, res);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String response = "";
        try {
            response = executor.execute(getBody(req));
        } catch (DispatchException e) {
            response = e.getError();
            res.setStatus(e.getStatusCode());
        }

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

    private String getBody(HttpServletRequest req) throws IOException {
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
