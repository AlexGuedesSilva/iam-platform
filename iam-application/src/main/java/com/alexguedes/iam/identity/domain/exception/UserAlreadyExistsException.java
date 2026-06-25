package com.alexguedes.iam.identity.domain.exception;

public class UserAlreadyExistsException extends DomainException {

    private static final String DEFAULT_MESSAGE = "User already exists";

    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}