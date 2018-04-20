package io.dispatchframework.javabaseimage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.Test;

public class ServerTest {
    private static final String packageName = "io.dispatchframework.javabaseimage";

    @Test
    public void getBiFunctionTypes_WithInterface() throws Exception {
        Server.ExecFunction goodHandler = new Server.ExecFunction(packageName, "GoodHandler");
        assertEquals(3, goodHandler.biFunctionTypes.length);
        assertEquals("java.util.Map<java.lang.String, java.lang.Object>", goodHandler.biFunctionTypes[0].getTypeName());
        assertEquals("java.util.List<java.lang.String>", goodHandler.biFunctionTypes[1].getTypeName());
        assertEquals("java.util.Set<java.lang.Integer>", goodHandler.biFunctionTypes[2].getTypeName());
    }

    @Test
    public void getBiFunctionTypes_WithoutInterface() {
        assertThrows(IllegalArgumentException.class, () -> new Server.ExecFunction(packageName, "BadHandler"));
    }

    @Test
    public void applyFunction_Valid() throws Exception {
        Server.ExecFunction hello = new Server.ExecFunction(packageName, "Hello");
        String message = "{\"context\": null, \"payload\": {\"name\": \"Jon\", \"place\": \"Winterfell\"}}";
        Object r = hello.applyFunction(message);

        assertTrue(r instanceof String);
        assertEquals("Hello, Jon from Winterfell", r);
    }

    @Test
    public void applyFunction_Empty() throws Exception {
        Server.ExecFunction hello = new Server.ExecFunction(packageName, "Hello");
        String message = "{}";
        Object r = hello.applyFunction(message);

        assertTrue(r instanceof String);
        assertEquals("Hello, Someone from Somewhere", r);
    }

    @Test
    public void applyFunction_InvalidType() throws Exception {
        Server.ExecFunction hello = new Server.ExecFunction(packageName, "Hello");
        String message = "{\"context\": null, \"payload\": \"invalid\"}";

        assertThrows(JsonSyntaxException.class, () -> hello.applyFunction(message));
    }

    @Test
    public void applyFunction_InvalidJson() throws Exception {
        Server.ExecFunction hello = new Server.ExecFunction(packageName, "Hello");
        String message = "{";

        assertThrows(JsonParseException.class, () -> hello.applyFunction(message));
    }
}