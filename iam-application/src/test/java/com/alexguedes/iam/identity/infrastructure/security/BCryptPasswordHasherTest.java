package com.alexguedes.iam.identity.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher passwordHasher =
            new BCryptPasswordHasher(new BCryptPasswordEncoder());

    @Test
    void shouldHashPassword() {
        String rawPassword = "StrongPassword123!";

        PasswordHash passwordHash = passwordHasher.hash(rawPassword);

        assertNotNull(passwordHash);
        assertNotEquals(rawPassword, passwordHash.value());
        assertTrue(passwordHash.value().matches("^\\$2[aby]\\$\\d{2}\\$.{53}$"));
    }

    @Test
    void shouldMatchValidPassword() {
        String rawPassword = "StrongPassword123!";
        PasswordHash passwordHash = passwordHasher.hash(rawPassword);

        boolean matches = passwordHasher.matches(rawPassword, passwordHash);

        assertTrue(matches);
    }

    @Test
    void shouldNotMatchInvalidPassword() {
        String rawPassword = "StrongPassword123!";
        PasswordHash passwordHash = passwordHasher.hash(rawPassword);

        boolean matches = passwordHasher.matches("DifferentPassword123!", passwordHash);

        assertFalse(matches);
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "StrongPassword123!";

        PasswordHash firstHash = passwordHasher.hash(rawPassword);
        PasswordHash secondHash = passwordHasher.hash(rawPassword);

        assertNotEquals(firstHash.value(), secondHash.value());
        assertTrue(passwordHasher.matches(rawPassword, firstHash));
        assertTrue(passwordHasher.matches(rawPassword, secondHash));
    }
}
