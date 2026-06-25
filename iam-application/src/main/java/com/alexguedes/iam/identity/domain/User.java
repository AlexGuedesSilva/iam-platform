package com.alexguedes.iam.identity.domain;

import java.time.Instant;
import java.util.Objects;

public class User {

    private final UserId id;
    private Email email;
    private PasswordHash passwordHash;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UserId id, Email email, PasswordHash passwordHash) {
        this(id, email, passwordHash, UserStatus.ACTIVE, Instant.now(), Instant.now());
    }

    public User(UserId id, Email email, PasswordHash passwordHash, UserStatus status, Instant createdAt, Instant updatedAt) {
        this.id = requireNonNull(id, "User id must not be null");
        this.email = requireNonNull(email, "Email must not be null");
        this.passwordHash = requireNonNull(passwordHash, "Password hash must not be null");
        this.status = requireNonNull(status, "User status must not be null");
        this.createdAt = requireNonNull(createdAt, "Created at must not be null");
        this.updatedAt = requireNonNull(updatedAt, "Updated at must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at must not be before created at");
        }
    }

    public UserId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public UserStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public void changeEmail(Email newEmail) {
        Email validEmail = requireNonNull(newEmail, "Email must not be null");
        if (email.equals(validEmail)) {
            return;
        }
        email = validEmail;
        markUpdated();
    }

    public void changePasswordHash(PasswordHash newPasswordHash) {
        PasswordHash validPasswordHash = requireNonNull(newPasswordHash, "Password hash must not be null");
        if (passwordHash.equals(validPasswordHash)) {
            return;
        }
        passwordHash = validPasswordHash;
        markUpdated();
    }

    public void activate() {
        if (status == UserStatus.ACTIVE) {
            return;
        }
        status = UserStatus.ACTIVE;
        markUpdated();
    }

    public void deactivate() {
        if (status == UserStatus.INACTIVE) {
            return;
        }
        status = UserStatus.INACTIVE;
        markUpdated();
    }

    public void block() {
        if (status == UserStatus.BLOCKED) {
            return;
        }
        status = UserStatus.BLOCKED;
        markUpdated();
    }

    private void markUpdated() {
        updatedAt = Instant.now();
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User user)) {
            return false;
        }
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
