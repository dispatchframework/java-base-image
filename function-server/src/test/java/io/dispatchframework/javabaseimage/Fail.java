package io.dispatchframework.javabaseimage;

import java.util.Map;
import java.util.function.BiFunction;

public class Fail implements BiFunction<Map<String, Object>, Map<String, Object>, String> {
    @Override
    public String apply(Map<String, Object> context, Map<String, Object> payload) {
        throw new RuntimeException();
    }
}
