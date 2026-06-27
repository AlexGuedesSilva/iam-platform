package com.alexguedes.iam.identity.domain;

import com.alexguedes.iam.identity.domain.exception.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final String VALID_NAME = "Alex Guedes";
    private static final Email VALID_EMAIL = new Email("alex@email.com");
    private static final UserId VALID_USER_ID = newUserId();
    private static final PasswordHash VALID_PASSWORD_HASH = new PasswordHash("12345678901234567890123456789012");

    @Test
    void shouldCreateActiveUser() {
        User user = newUser();

        assertEquals(VALID_NAME, user.name());
        assertEquals(UserStatus.ACTIVE, user.status());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCreateUserWithAuditFields() {
        UserId id = newUserId();
        Email email = new Email("alex@email.com");
        PasswordHash passwordHash = new PasswordHash("12345678901234567890123456789012");
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:30:00Z");

        User user = new User(
                id,
                "Alex Guedes",
                email,
                passwordHash,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        assertEquals(id, user.id());
        assertEquals(createdAt, user.createdAt());
        assertEquals(updatedAt, user.updatedAt());
    }

    @Test
    void shouldRejectUpdatedAtBeforeCreatedAt() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T09:59:59Z");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        newUserId(),
                        VALID_NAME,
                        VALID_EMAIL,
                        VALID_PASSWORD_HASH,
                        UserStatus.ACTIVE,
                        createdAt,
                        updatedAt
                )
        );

        assertEquals("Updated at must not be before created at", exception.getMessage());
    }

    @Test
    void shouldNotUpdateUserWhenRenamingWithSameName() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");

        User user = new User(
                newUserId(),
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD_HASH,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        user.rename(VALID_NAME);

        assertEquals(VALID_NAME, user.name());
        assertEquals(updatedAt, user.updatedAt());
    }

    @Test
    void shouldAllowChangeUserName() {
        User user = newUser();

        String newName = "Leonardo da Vinci";
        user.rename(newName);

        assertEquals(newName, user.name());
    }

    @Test
    void shouldDisableUser() {
        User user = newUser();

        user.deactivate();

        assertEquals(UserStatus.INACTIVE, user.status());
        assertFalse(user.isActive());
        assertThrows(UserAlreadyDisabledException.class, () -> user.deactivate());

    }

    @Test
    void shouldActivateUser() {
        User user = newUser();
        user.deactivate();

        user.activate();

        assertEquals(UserStatus.ACTIVE, user.status());
        assertTrue(user.isActive());
        assertThrows(UserAlreadyActiveException.class, () -> user.activate());
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
    void shouldChangeUserToBlocked() {
        User user = newUser();
        user.block();

        assertEquals(UserStatus.BLOCKED, user.status());
    }

    @Test
    void shouldRejectUserNameShorterThanMinimumLength() {
        User user = newUser();

        assertThrows(InvalidUserNameException.class, () -> user.rename("b"));
    }

    @Test
    void shouldRejectUserNameBiggerThanMaximumLength() {
        User user = newUser();
        String bigString =
                "ahjebsjbhsdlbhbdvhjdfvbhjdvbjdfvbhjdkjsbvksbdvhksbdhkvsbnnksjdncksdjncskjdncskjdcnskdjcnsdjkcndkhvbsdhkv";

        assertThrows(InvalidUserNameException.class, () -> user.rename(bigString));
    }

    @Test
    void shouldNotAllowInvalidName() {

        assertThrows(InvalidUserNameException.class, () -> new User(newUserId(), " ", new Email("user@example.com"), VALID_PASSWORD_HASH));
        assertThrows(InvalidUserNameException.class, () -> new User(newUserId(), null, new Email("user@example.com"), VALID_PASSWORD_HASH));
    }

    @Test
    void shouldNotAllowNullEmail() {
        assertThrows(InvalidEmailException.class, () -> new User(newUserId(), VALID_NAME, null, VALID_PASSWORD_HASH));
    }

    @Test
    void shouldNotAllowNullPasswordHash() {
        assertThrows(InvalidPasswordException.class, () -> new User(newUserId(), VALID_NAME, new Email("user@example.com"), null));
    }

    @Test
    void shouldNotAllowNullUserId() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, VALID_NAME, VALID_EMAIL, VALID_PASSWORD_HASH));
    }

    @Test
    void shouldBeEqualToItself() {
        User user = newUser();
        User user2 = user;

        assertTrue(user.equals(user2));
    }

    @Test
    void shouldNotBeEqualToDifferentObjectType() {
        User user = newUser();

        assertFalse(user.equals("not-a-user"));
    }

    @Test
    void shouldBeEqualWhenUsersHaveSameId() {
        UserId sameId = newUserId();

        User user1 = new User(
                sameId,
                "Alex Guedes",
                new Email("alex@email.com"),
                new PasswordHash("12345678901234567890123456789012")
        );

        User user2 = new User(
                sameId,
                "Outro Nome",
                new Email("outro@email.com"),
                new PasswordHash("abcdefghijklmnopqrstuvwxyz123456")
        );

        assertEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualWhenUsersHaveDifferentIds() {
        User user1 = new User(
                newUserId(),
                "Alex Guedes",
                new Email("alex@email.com"),
                new PasswordHash("12345678901234567890123456789012")
        );

        User user2 = new User(
                newUserId(),
                "Alex Guedes",
                new Email("alex@email.com"),
                new PasswordHash("12345678901234567890123456789012")
        );

        assertNotEquals(user1, user2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        User user = newUser();

        assertNotEquals(null, user);
    }

    @Test
    void shouldHaveSameHashCodeWhenUsersHaveSameId() {
        UserId sameId = newUserId();

        User user1 = new User(
                sameId,
                "Alex Guedes",
                new Email("alex@email.com"),
                new PasswordHash("12345678901234567890123456789012")
        );

        User user2 = new User(
                sameId,
                "Outro Nome",
                new Email("outro@email.com"),
                new PasswordHash("abcdefghijklmnopqrstuvwxyz123456")
        );

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldRejectNullUpdatedAt() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        newUserId(),
                        VALID_NAME,
                        VALID_EMAIL,
                        VALID_PASSWORD_HASH,
                        UserStatus.ACTIVE,
                        Instant.now(),
                        null
                )
        );
    }

    @Test
    void shouldRejectNullCreatedAt() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        newUserId(),
                        VALID_NAME,
                        VALID_EMAIL,
                        VALID_PASSWORD_HASH,
                        UserStatus.ACTIVE,
                        null,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectNullStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User(
                        newUserId(),
                        VALID_NAME,
                        VALID_EMAIL,
                        VALID_PASSWORD_HASH,
                        null,
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    @Test
    void shouldNotUpdateWhenUserIsAlreadyBlocked() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");

        User user = new User(
                newUserId(),
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD_HASH,
                UserStatus.BLOCKED,
                createdAt,
                updatedAt
        );

        user.block();

        assertEquals(UserStatus.BLOCKED, user.status());
        assertEquals(updatedAt, user.updatedAt());
    }

    @Test
    void shouldNotUpdateWhenChangingToSamePasswordHash() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");

        User user = new User(
                newUserId(),
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD_HASH,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        user.changePasswordHash(VALID_PASSWORD_HASH);

        assertEquals(VALID_PASSWORD_HASH, user.passwordHash());
        assertEquals(updatedAt, user.updatedAt());
    }

    @Test
    void shouldNotUpdateWhenChangingToSameEmail() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");

        User user = new User(
                newUserId(),
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD_HASH,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        user.changeEmail(VALID_EMAIL);

        assertEquals(VALID_EMAIL, user.email());
        assertEquals(updatedAt, user.updatedAt());
    }

    @Test
    void shouldNotUpdateWhenRenamingWithSameName() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T10:00:00Z");

        User user = new User(
                newUserId(),
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD_HASH,
                UserStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        user.rename(VALID_NAME);

        assertEquals(VALID_NAME, user.name());
        assertEquals(updatedAt, user.updatedAt());
    }

    private static User newUser() {
        return new User(VALID_USER_ID, VALID_NAME, VALID_EMAIL, VALID_PASSWORD_HASH);
    }

    private static UserId newUserId() {
        return new UserId(UUID.randomUUID());
    }
}
