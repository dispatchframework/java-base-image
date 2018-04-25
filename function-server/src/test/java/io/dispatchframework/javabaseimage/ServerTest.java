package io.dispatchframework.javabaseimage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    @Test
    public void processMessage_Valid() throws Exception {
        Server.ExecFunction hello = new Server.ExecFunction(packageName, "Hello");
        String message = "{\"context\": null, \"payload\": {\"name\": \"Jon\", \"place\": \"Winterfell\"}}";

        Response response = hello.processMessage(message);

        assertTrue(response.getPayload() instanceof String);
        assertEquals("Hello, Jon from Winterfell", response.getPayload());
        assertEquals(0, response.getContext().getLogs().getStdout().length);
        assertEquals(0, response.getContext().getLogs().getStderr().length);
        assertNull(response.getContext().getError());
    }

    @Test
    public void processMessage_FunctionException() throws Exception {
        Server.ExecFunction fail = new Server.ExecFunction(packageName, "Fail");
        String message = "{\"context\": null, \"payload\": null}";

        Response response = fail.processMessage(message);

        String[] stderrLogs;
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            response.getContext().getError().printStackTrace(pw);

            stderrLogs = sw.toString().split("\\r?\\n");
        }

        assertNull(response.getPayload());
        assertTrue(response.getContext().getError() instanceof RuntimeException);
        assertArrayEquals(stderrLogs, response.getContext().getLogs().getStderr());
        assertEquals(0, response.getContext().getLogs().getStdout().length);
    }

    @Test
    public void processMessage_CheckLogs() throws Exception {
        Server.ExecFunction logger = new Server.ExecFunction(packageName, "Logger");
        String message = "{\"context\": null, \"payload\": null}";

        Response response = logger.processMessage(message);

        assertArrayEquals(new String[]{"stderr", "stderr2"}, response.getContext().getLogs().getStderr());
        assertArrayEquals(new String[]{"stdout", "stdout2"}, response.getContext().getLogs().getStdout());
        assertNull(response.getContext().getError());
    }
}