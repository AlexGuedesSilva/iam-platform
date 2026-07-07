package com.alexguedes.iam.identity.interfaces.rest.authentication;

import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserResult;

public record LoginResponse(
        String userId,
        String email,
        String status
) {

    static LoginResponse from(LoginUserResult result) {
        return new LoginResponse(
                result.userId().value().toString(),
                result.email().value(),
                result.status().name()
        );
    }
}
