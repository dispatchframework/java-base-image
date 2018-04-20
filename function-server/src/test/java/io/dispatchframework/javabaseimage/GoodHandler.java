package io.dispatchframework.javabaseimage;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class GoodHandler implements Serializable, BiFunction<Map<String, Object>, List<String>, Set<Integer>>, BiPredicate<String, String> {
    @Override
    public Set<Integer> apply(Map<String, Object> context, List<String> payload)  {
        return new HashSet<>();
    }

    @Override
    public boolean test(String s1, String s2) {
        return true;
    }
}
