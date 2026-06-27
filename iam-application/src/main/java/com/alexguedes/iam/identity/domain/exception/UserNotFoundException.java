package com.alexguedes.iam.identity.domain.exception;

public class UserNotFoundException extends DomainException {

    private static final String DEFAULT_MESSAGE = "User was not found";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}