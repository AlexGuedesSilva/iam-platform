package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;
import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
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
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                new PasswordHash(null));

        assertEquals("Password hash must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectBlankHash() {
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                new PasswordHash("  "));

        assertEquals("Password hash must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectPasswordHashShorterThanMinimumLength() {
        String shortHash = "1234567890123456789012345678901"; // 31 caracteres

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () ->
                new PasswordHash(shortHash));

        assertEquals("Password hash must have at least " + 32 + " characters", exception.getMessage());
    }
}