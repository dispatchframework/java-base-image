///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

public class Context {
    private Error error;
    private Logs logs;

    public Context(Error error, Logs logs) {
        this.error = error;
        this.logs = logs;
    }

    public Error getError() {
        return error;
    }

    public Logs getLogs() {
        return logs;
    }
}
