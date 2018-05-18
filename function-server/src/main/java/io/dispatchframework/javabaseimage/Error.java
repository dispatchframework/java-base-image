///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Error {
    private ErrorType type;
    private String message;
    private String[] stacktrace;

    public Error(ErrorType type, String message, String[] stacktrace) {
        this.type = type;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public Error(Exception ex, ErrorType type) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            ex.printStackTrace(pw);

            this.type = type;
            this.message = ex.getMessage();
            this.stacktrace = sw.toString().split("\\r?\\n");
        }
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String[] getStacktrace() {
        return stacktrace;
    }
}
