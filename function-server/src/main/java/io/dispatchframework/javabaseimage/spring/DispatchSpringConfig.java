/**
 * 
 */
package io.dispatchframework.javabaseimage.spring;

import java.util.function.BiFunction;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.dispatchframework.javabaseimage.servlet.HealthzServlet;
import io.dispatchframework.javabaseimage.servlet.SpringFunctionServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 *
 */
@Configuration
@ComponentScan("io.dispatchframework")
public class DispatchSpringConfig {

	@Bean("springServlet")
	Servlet springServlet(BiFunction f) {
		return new SpringFunctionServlet(f);
	}

	@Bean
	Servlet healthzServlet() {
		return new HealthzServlet();
	}

	@Bean(name = "deploymentInfo")
	DeploymentInfo deploymentInfo(@Qualifier("springServlet") Servlet springServlet,
			@Qualifier("healthzServlet") Servlet healthzServlet) {
		return Servlets.deployment()
				.setClassLoader(DispatchSpringConfig.class.getClassLoader())
				.setDeploymentName("dispatch")
				.setContextPath("/ROOT")
				.addServlet(Servlets
						.servlet("spring", SpringFunctionServlet.class, new ImmediateInstanceFactory<>(springServlet))
						.addMapping("/"))
				.addServlet(Servlets
						.servlet("healthz", HealthzServlet.class, new ImmediateInstanceFactory<>(healthzServlet))
						.addMapping("/healthz"));
	}

	@Bean(name = "deploymentManager", initMethod = "deploy", destroyMethod = "undeploy")
	DeploymentManager deploymentManager(DeploymentInfo deploymentInfo) {
		return Servlets.defaultContainer().addDeployment(deploymentInfo);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	Undertow undertow(DeploymentManager manager) throws ServletException {

		PathHandler path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());

		return Undertow.builder().addHttpListener(8080, "localhost").setHandler(path).build();
	}
}