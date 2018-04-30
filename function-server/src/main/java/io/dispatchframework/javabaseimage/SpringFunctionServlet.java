/* **********************************************************
 * SpringFunctionExecutor.java
 *
 * Copyright (C) 2018 VMware, Inc.
 * All Rights Reserved
 * **********************************************************/
package io.dispatchframework.javabaseimage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.BiFunction;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 */
public class SpringFunctionServlet implements Servlet {

	private BiFunction f;
	private FunctionExecutor executor;

	public SpringFunctionServlet(BiFunction f) {
		this.f = f;
		this.executor = new SimpleFunctionExecutor(f);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String response = executor.execute(getBody(req));
		
		try (Writer writer = res.getWriter()) {
			writer.append(response);
			res.setContentType("application/json");
		}
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	private String getBody(ServletRequest req) throws IOException {
		String body = null;
		StringBuffer sb = new StringBuffer();
		
		try (Reader reader = req.getReader()) {
			char[] charBuffer = new char[128];
			int bytesRead = -1;
			while ((bytesRead = reader.read(charBuffer)) > 0) {
				sb.append(charBuffer, 0, bytesRead);
			}
		}

		body = sb.toString();
		return body;
	}
}
