/**
 * 
 */
package io.dispatchframework.javabaseimage;

/**
 * An Exception that wraps internal Dispatch errors
 */
public class DispatchException extends Exception {

    private final int statusCode;
    private final String error;

    public DispatchException(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getError() {
        return this.error;
    }
}
