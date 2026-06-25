package com.alexguedes.iam.identity.domain;

import java.time.Instant;
import java.util.Objects;

public class User {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 120;

    private final UserId id;
    private String name;
    private Email email;
    private PasswordHash passwordHash;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UserId id, String name, Email email, PasswordHash passwordHash) {
        this(id, name, email, passwordHash, UserStatus.ACTIVE, Instant.now(), Instant.now());
    }

    public User(UserId id, String name, Email email, PasswordHash passwordHash, UserStatus status, Instant createdAt, Instant updatedAt) {
        this.id = requireNonNull(id, "User id must not be null");
        this.name = validateName(name);
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

    public String name() {
        return name;
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

    public void rename(String newName) {
        String validName = validateName(newName);
        if (name.equals(validName)) {
            return;
        }
        name = validName;
        markUpdated();
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

    private static String validateName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User name must not be blank");
        }

        String normalized = value.trim();
        if (normalized.length() < MIN_NAME_LENGTH || normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("User name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
        }

        return normalized;
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