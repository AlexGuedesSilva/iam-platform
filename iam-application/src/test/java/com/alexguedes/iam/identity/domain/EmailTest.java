package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = new Email("user@example.com");

        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        Email email = new Email("USER@EXAMPLE.COM");

        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldRejectNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void shouldRejectBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
    }
}