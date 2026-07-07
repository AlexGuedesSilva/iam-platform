package com.alexguedes.iam.identity.application.exception;

public class AuthenticationFailedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Invalid credentials";

    public AuthenticationFailedException() {
        super(DEFAULT_MESSAGE);
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
