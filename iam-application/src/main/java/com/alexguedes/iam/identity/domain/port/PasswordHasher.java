package com.alexguedes.iam.identity.domain.port;

import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;

public interface PasswordHasher {

    PasswordHash hash(String rawPassword);
}