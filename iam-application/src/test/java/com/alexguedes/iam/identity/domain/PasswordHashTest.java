package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordHashTest {

    @Test
    void shouldCreateValidPasswordHash() {
        String value = "12345678901234567890123456789012";

        PasswordHash passwordHash = new PasswordHash(value);

        assertEquals(value, passwordHash.value());
    }

    @Test
    void shouldRejectNullHash() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordHash(null));
    }

    @Test
    void shouldRejectBlankHash() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordHash("   "));
    }
}