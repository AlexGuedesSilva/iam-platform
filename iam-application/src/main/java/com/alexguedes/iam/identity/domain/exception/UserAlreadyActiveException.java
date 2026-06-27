package com.alexguedes.iam.identity.domain.exception;

public class UserAlreadyActiveException extends DomainException {

    private static final String DEFAULT_MESSAGE = "User is already active";

    public UserAlreadyActiveException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyActiveException(String message) {
        super(message);
    }
}