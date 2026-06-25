package com.alexguedes.iam.identity.domain;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User id must not be blank");
        }
        return new UserId(UUID.fromString(value));
    }

}
