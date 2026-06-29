package com.alexguedes.iam.identity.application;

import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import java.time.Instant;

public record RegisterUserResult(UserId userId, String name, Email email, UserStatus status, Instant createdAt) {
}
