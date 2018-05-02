///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

public class Response {
    private Context context;
    private Object payload;

    public Response(Context context, Object payload) {
        this.context = context;
        this.payload = payload;
    }

    public Context getContext() {
        return context;
    }

    public Object getPayload() {
        return payload;
    }
}
