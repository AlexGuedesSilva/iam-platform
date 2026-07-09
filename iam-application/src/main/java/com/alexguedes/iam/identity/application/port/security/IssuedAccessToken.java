package com.alexguedes.iam.identity.application.port.security;

import java.time.Instant;

public record IssuedAccessToken(String value, String tokenType, Instant expiresAt) {

    public IssuedAccessToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Access token value must not be blank");
        }
        if (tokenType == null || tokenType.isBlank()) {
            throw new IllegalArgumentException("Token type must not be blank");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expires at must not be null");
        }
    }
}
