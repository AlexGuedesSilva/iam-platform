package com.alexguedes.iam.identity.interfaces.rest.authentication;

import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserResult;

public record LoginResponse(
        String userId,
        String email,
        String status,
        String accessToken,
        String tokenType,
        String expiresAt
) {

    static LoginResponse from(LoginUserResult result) {
        return new LoginResponse(
                result.userId().value().toString(),
                result.email().value(),
                result.status().name(),
                result.accessToken(),
                result.tokenType(),
                result.expiresAt().toString()
        );
    }
}
