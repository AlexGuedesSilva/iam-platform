package com.alexguedes.iam.identity.domain.exception;

public class UserAlreadyDisabledException extends DomainException {

    private static final String DEFAULT_MESSAGE = "User is already disabled";

    public UserAlreadyDisabledException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyDisabledException(String message) {
        super(message);
    }
}