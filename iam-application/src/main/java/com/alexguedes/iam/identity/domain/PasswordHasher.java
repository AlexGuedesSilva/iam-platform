package com.alexguedes.iam.identity.domain;

public interface PasswordHasher {

    PasswordHash hash(String rawPassword);
}