///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage.Handlers;

import java.io.Serializable;
import java.util.function.BiPredicate;

public class BadHandler implements Serializable, BiPredicate<String, String> {
    @Override
    public boolean test(String s1, String s2) {
        return true;
    }
}
