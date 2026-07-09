package com.alexguedes.iam.identity.application.port.security;

import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import java.time.Instant;

public record AccessTokenClaims(
        UserId subject,
        Email email,
        UserStatus status,
        String issuer,
        Instant issuedAt,
        Instant expiresAt,
        String tokenType
) {

    public AccessTokenClaims {
        if (subject == null) {
            throw new IllegalArgumentException("Subject must not be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("Issuer must not be blank");
        }
        if (issuedAt == null) {
            throw new IllegalArgumentException("Issued at must not be null");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expires at must not be null");
        }
        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException("Expires at must be after issued at");
        }
        if (tokenType == null || tokenType.isBlank()) {
            throw new IllegalArgumentException("Token type must not be blank");
        }
    }
}
