package com.alexguedes.iam.identity.domain.valueobject;

import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;

public record PasswordHash(String value) {

    private static final int MIN_LENGTH = 32;

    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new InvalidPasswordException("Password hash must not be blank");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH) {
            throw new InvalidPasswordException(
                    "Password hash must have at least " + MIN_LENGTH + " characters"
            );
        }
    }
}