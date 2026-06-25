package com.alexguedes.iam.identity.domain;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Email must be valid");
        }

        this.value = normalized;
    }
}
