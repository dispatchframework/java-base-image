package io.dispatchframework.javabaseimage;

public class Context {
    private Exception error;
    private Logs logs;

    public Context(Exception error, Logs logs) {
        this.error = error;
        this.logs = logs;
    }

    public Exception getError() {
        return error;
    }

    public Logs getLogs() {
        return logs;
    }
}
