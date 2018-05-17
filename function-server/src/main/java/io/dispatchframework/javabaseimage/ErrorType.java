///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import com.google.gson.annotations.SerializedName;

public enum ErrorType {
    @SerializedName("InputError")
    INPUT_ERROR("InputError"),

    @SerializedName("FunctionError")
    FUNCTION_ERROR("FunctionError"),

    @SerializedName("SystemError")
    SYSTEM_ERROR("SystemError");

    private final String type;

    ErrorType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
