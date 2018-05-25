///////////////////////////////////////////////////////////////////////
// Copyright (c) 2018 VMware, Inc. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
///////////////////////////////////////////////////////////////////////
package io.dispatchframework.javabaseimage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTests {
    private static final String packageName = "io.dispatchframework.javabaseimage.handlers";

    @Test
    public void validPOJFunction_noError() throws Exception {
        Validator.main(new String[] {String.format("%s.%s", packageName, "GoodHandler")});
    }

    @Test
    public void nonExistentClass_throws_ClassNotFoundException() {
        assertThrows(ClassNotFoundException.class, () -> Validator.main(new String[] {"NonExistentClass"}));
    }

    @Test
    public void noBiFunctionInterface_throws_ClassCastException() {
        assertThrows(ClassCastException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "BadHandler")}));
    }

    @Test
    public void abstractClass_throws_InstantiationException() {
        assertThrows(InstantiationException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "AbstractHandler")}));
    }

    @Test
    public void privateConstructor_throws_IllegalAccessException() {
        assertThrows(IllegalAccessException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "PrivateHandler")}));
    }

    @Test
    public void validSpringFunction_noError() throws Exception {
        Validator.main(new String[] {String.format("%s.%s", packageName, "SpringHandler")});
    }

    @Test
    public void unannotatedClass_throws_UnsatisfiedDependencyException() {
        assertThrows(UnsatisfiedDependencyException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "UnannotatedSpringHandler")}));
    }

    @Test
    public void privateSpringConstructor_throws_IllegalStateException() {
        assertThrows(IllegalStateException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "PrivateSpringHandler")}));
    }

    @Test
    public void invalidBean_throws_BeanDefinitionParsingException() {
        assertThrows(BeanDefinitionParsingException.class, () -> Validator.main(new String[] {String.format("%s.%s", packageName, "InvalidBean")}));
    }
}
