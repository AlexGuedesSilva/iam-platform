package com.alexguedes.iam.identity.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateValidUserId() {
        UUID value = UUID.randomUUID();

        UserId userId = new UserId(value);

        assertEquals(value, userId.value());
    }

    @Test
    void shouldRejectNullId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserId(null)
        );

        assertEquals("User id must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectEmptyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserId.fromString("")
        );

        assertEquals("User id must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectBlankString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserId.fromString(" ")
        );

        assertEquals("User id must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectNullString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserId.fromString(null)
        );

        assertEquals("User id must not be blank", exception.getMessage());
    }

    @Test
    void shouldCreateUserIdFromValidString() {
        UUID value = UUID.randomUUID();
        String stringValue = value.toString();

        UserId userId = UserId.fromString(stringValue);

        assertEquals(value, userId.value());
    }

    @Test
    void shouldRejectInvalidUuidString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> UserId.fromString("invalid-uuid")
        );
    }
}
