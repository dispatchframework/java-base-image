///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import java.util.function.BiFunction;

/**
 * Configuration used to validate user given spring handler.
 */
@Configuration
public class SpringServletConfig {
    @Bean("springServlet")
    Servlet springServlet(BiFunction f) {
        return new SpringFunctionServlet(f);
    }
}
