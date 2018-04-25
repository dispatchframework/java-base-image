package io.dispatchframework.javabaseimage;

public class Logs {
    private String[] stderr;
    private String[] stdout;

    public Logs(String[] stderr, String[] stdout) {
        this.stderr = stderr;
        this.stdout = stdout;
    }

    public String[] getStderr() {
        return stderr;
    }

    public String[] getStdout() {
        return stdout;
    }
}
