package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = new Email("user@example.com");

        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        Email email = new Email("  USER@EXAMPLE.COM  ");

        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldRejectNullEmail() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> new Email(null));

        assertEquals("Email must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectBlankEmail() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> new Email("   "));

        assertEquals("Email must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> new Email("invalid-email"));

        assertEquals("Email format is invalid", exception.getMessage());
    }
}