///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.util.function.BiFunction;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * Server configuration for Dispatch functions using Spring
 */
@Configuration
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
        return Servlets.deployment().setClassLoader(DispatchSpringConfig.class.getClassLoader())
                .setDeploymentName("dispatch").setContextPath("/ROOT")
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
    Undertow undertow(DeploymentManager manager, @Value("${PORT:8080}") int port) throws ServletException {

        PathHandler path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());

        if (port == 0) {
            port = 8080;
        }
        return Undertow.builder().addHttpListener(port, "0.0.0.0").setHandler(path).build();
    }
}
