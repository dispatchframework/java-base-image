package io.dispatchframework.javabaseimage;

import java.util.Map;
import java.util.function.BiFunction;

public class Dummy implements BiFunction<Map<String, Object>, Object, Object> {
    public Object apply(Map<String, Object> context, Object payload) {
        return payload;
    }
}
