///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

public class Request {
    private Object context;
    private Object payload;

    public Request(Object context, Object payload) {
        this.context = context;
        this.payload = payload;
    }

    public Object getContext() {
        return context;
    }

    public Object getPayload() {
        return payload;
    }
}
