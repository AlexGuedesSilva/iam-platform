package com.alexguedes.iam.identity.domain;

import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;

public record PasswordHash(String value) {

    private static final int MIN_LENGTH = 32;

    public PasswordHash(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidPasswordException("Password hash must not be blank");
        }

        String normalized = value.trim();
        if (normalized.length() < MIN_LENGTH) {
            throw new InvalidPasswordException("Password hash must have at least " + MIN_LENGTH + " characters");
        }

        this.value = normalized;
    }

}