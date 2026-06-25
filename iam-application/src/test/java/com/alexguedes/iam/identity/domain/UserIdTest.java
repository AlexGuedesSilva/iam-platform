package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserIdTest {

    @Test
    void shouldCreateValidUserId() {
        UUID value = UUID.randomUUID();

        UserId userId = new UserId(value);

        assertEquals(value, userId.value());
    }

    @Test
    void shouldCreateNewUserId() {
        UserId userId = UserId.newId();

        assertNotNull(userId.value());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }
}