package io.dispatchframework.javabaseimage;

public class Context {
    private Exception error;
    private String[] logs;

    public Context(Exception error, String[] logs) {
        this.error = error;
        this.logs = logs;
    }

    public Exception getError() {
        return error;
    }

    public String[] getLogs() {
        return logs;
    }
}
