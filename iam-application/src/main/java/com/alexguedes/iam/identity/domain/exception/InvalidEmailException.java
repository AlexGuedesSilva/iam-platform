package com.alexguedes.iam.identity.domain.exception;

public class InvalidEmailException extends DomainException {

    private static final String DEFAULT_MESSAGE = "Email is invalid";

    public InvalidEmailException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}