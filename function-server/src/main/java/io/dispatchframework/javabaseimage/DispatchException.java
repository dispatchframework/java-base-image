/**
 * 
 */
package io.dispatchframework.javabaseimage;

/**
 * An Exception that wraps internal Dispatch errors
 */
public class DispatchException extends Exception {

    private final Error error;

    public DispatchException(Error error) {
        this.error = error;
    }

    public Error getError() {
        return this.error;
    }
}
