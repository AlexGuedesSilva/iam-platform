package com.alexguedes.iam.identity.application.port.security;

import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;

public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash passwordHash);
}