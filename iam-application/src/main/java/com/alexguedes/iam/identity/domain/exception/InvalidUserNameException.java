package com.alexguedes.iam.identity.domain.exception;

public class InvalidUserNameException extends DomainException {

    private static final String DEFAULT_MESSAGE = "User name is invalid";

    public InvalidUserNameException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidUserNameException(String message) {
        super(message);
    }
}