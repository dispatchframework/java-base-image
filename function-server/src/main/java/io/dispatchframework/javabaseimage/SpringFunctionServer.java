/**
 * 
 */
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import io.dispatchframework.javabaseimage.spring.DispatchSpringConfig;

/**
 *
 */
public class SpringFunctionServer implements Server {

	private AbstractApplicationContext ctx;

	public void start() {
		ctx = new AnnotationConfigApplicationContext(DispatchSpringConfig.class);
	}
	
	public void stop() {
		ctx.close();
	}
}
