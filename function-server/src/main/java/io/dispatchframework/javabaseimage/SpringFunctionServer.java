/* **********************************************************
 * SpringFunctionServer.java
 *
 * Copyright (C) 2018 VMware, Inc.
 * All Rights Reserved
 * **********************************************************/
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 */
public class SpringFunctionServer implements Server {

	private Class clazz;
	
	private AnnotationConfigApplicationContext ctx;

	public SpringFunctionServer(String[] args) throws ClassNotFoundException {
		this.clazz = Class.forName(args[0] + "." + args[1]);
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
