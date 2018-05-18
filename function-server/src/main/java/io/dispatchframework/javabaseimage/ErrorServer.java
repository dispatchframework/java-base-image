///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/**
 * A Server implementation that returns an error response that
 * was encountered when initializing or running the function server.
 */
public class ErrorServer implements Server {
    private Undertow undertow;

    public ErrorServer(Exception ex, boolean springInClassPath) {
        HttpHandler errorHandler = (springInClassPath) ? new HttpHandlers.SpringErrorHandler(ex) : new HttpHandlers.POJErrorHandler(ex);

        undertow = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(Handlers.path()
                .addPrefixPath("/", errorHandler).addExactPath("/healthz", new HttpHandlers.Healthz()))
                .build();
    }

    public void start() {
        this.undertow.start();
    }

    public void stop() {
        this.undertow.stop();
    }
}
