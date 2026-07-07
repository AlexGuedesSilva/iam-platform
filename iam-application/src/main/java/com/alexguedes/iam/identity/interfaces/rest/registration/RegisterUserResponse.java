package com.alexguedes.iam.identity.interfaces.rest.registration;

import com.alexguedes.iam.identity.application.usecase.registration.RegisterUserResult;
import java.time.Instant;

public record RegisterUserResponse(
        String id,
        String name,
        String email,
        String status,
        Instant createdAt
) {

    static RegisterUserResponse from(RegisterUserResult result) {
        return new RegisterUserResponse(
                result.userId().value().toString(),
                result.name(),
                result.email().value(),
                result.status().name(),
                result.createdAt()
        );
    }
}
