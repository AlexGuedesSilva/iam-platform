package com.alexguedes.iam.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UserTest {

    private static final String VALID_NAME = "Alex Guedes";
    private static final PasswordHash VALID_PASSWORD_HASH = new PasswordHash("12345678901234567890123456789012");

    @Test
    void shouldCreateActiveUser() {
        User user = newUser();

        assertEquals(VALID_NAME, user.name());
        assertEquals(UserStatus.ACTIVE, user.status());
        assertTrue(user.isActive());
    }

    @Test
    void shouldDisableUser() {
        User user = newUser();

        user.deactivate();

        assertEquals(UserStatus.INACTIVE, user.status());
    }

    @Test
    void shouldActivateUser() {
        User user = newUser();
        user.deactivate();

        user.activate();

        assertEquals(UserStatus.ACTIVE, user.status());
    }

    @Test
    void shouldChangeEmail() {
        User user = newUser();
        Email newEmail = new Email("new@example.com");

        user.changeEmail(newEmail);

        assertEquals(newEmail, user.email());
    }

    @Test
    void shouldChangePasswordHash() {
        User user = newUser();
        PasswordHash newPasswordHash = new PasswordHash("abcdefghijklmnopqrstuvwxyz123456");

        user.changePasswordHash(newPasswordHash);

        assertEquals(newPasswordHash, user.passwordHash());
    }

    @Test
    void shouldNotAllowInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new User(UserId.newId(), " ", new Email("user@example.com"), VALID_PASSWORD_HASH));
    }

    @Test
    void shouldNotAllowNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> new User(UserId.newId(), VALID_NAME, null, VALID_PASSWORD_HASH));
    }

    @Test
    void shouldNotAllowNullPasswordHash() {
        assertThrows(IllegalArgumentException.class, () -> new User(UserId.newId(), VALID_NAME, new Email("user@example.com"), null));
    }

    private static User newUser() {
        return new User(UserId.newId(), VALID_NAME, new Email("user@example.com"), VALID_PASSWORD_HASH);
    }
}