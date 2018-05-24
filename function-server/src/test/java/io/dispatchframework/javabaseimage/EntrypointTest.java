package io.dispatchframework.javabaseimage;

import io.dispatchframework.javabaseimage.handlers.Hello;
import io.dispatchframework.javabaseimage.handlers.SpringHandler;
import org.junit.jupiter.api.Test;

import static io.dispatchframework.javabaseimage.Entrypoint.isSpringAnnotated;
import static org.junit.jupiter.api.Assertions.*;

class EntrypointTest {

    @Test
    void isSpringAnnotatedTest() {
        assertTrue(isSpringAnnotated(SpringHandler.class));
        assertFalse(isSpringAnnotated(Hello.class));
    }
}