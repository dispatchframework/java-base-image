/* **********************************************************
 * Entrypoint.java
 *
 * Copyright (C) 2018 VMware, Inc.
 * All Rights Reserved
 * **********************************************************/
package io.dispatchframework.javabaseimage;

/**
 *
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
		if (springInClasspath) {
			// run spring function server
			server = new SpringFunctionServer(args);
		} else {
			// run plain old java function server
			server = new POJFunctionServer(args[0], args[1]);
		}

		server.start();
		synchronized (server) {
			server.wait();
		}

		server.stop();
	}

}
