package com.github.kokorin.jaffree;

public class JaffreeException extends RuntimeException {
    public JaffreeException() {
    }

    public JaffreeException(String message) {
        super(message);
    }

    public JaffreeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaffreeException(Throwable cause) {
        super(cause);
    }
}
