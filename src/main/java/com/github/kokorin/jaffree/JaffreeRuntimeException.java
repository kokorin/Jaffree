package com.github.kokorin.jaffree;

public class JaffreeRuntimeException extends RuntimeException {
    public JaffreeRuntimeException() {
    }

    public JaffreeRuntimeException(String message) {
        super(message);
    }

    public JaffreeRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaffreeRuntimeException(Throwable cause) {
        super(cause);
    }
}
