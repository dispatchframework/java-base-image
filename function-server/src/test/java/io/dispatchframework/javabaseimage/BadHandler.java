package io.dispatchframework.javabaseimage;

import java.io.Serializable;
import java.util.function.BiPredicate;

public class BadHandler implements Serializable, BiPredicate<String, String> {
    @Override
    public boolean test(String s1, String s2) {
        return true;
    }
}
