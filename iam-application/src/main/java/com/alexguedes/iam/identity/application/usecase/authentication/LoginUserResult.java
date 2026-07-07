package com.alexguedes.iam.identity.application.usecase.authentication;

import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import java.time.Instant;

public record LoginUserResult(UserId userId, String name, Email email, UserStatus status, Instant createdAt) {
}
