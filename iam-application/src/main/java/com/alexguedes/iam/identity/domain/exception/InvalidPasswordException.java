package com.alexguedes.iam.identity.domain.exception;

public class InvalidPasswordException extends DomainException {

    private static final String DEFAULT_MESSAGE = "Password is invalid";

    public InvalidPasswordException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}