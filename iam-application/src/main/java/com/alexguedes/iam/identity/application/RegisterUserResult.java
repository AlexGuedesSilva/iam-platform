package com.alexguedes.iam.identity.application;

import com.alexguedes.iam.identity.domain.Email;
import com.alexguedes.iam.identity.domain.UserId;
import com.alexguedes.iam.identity.domain.UserStatus;

public record RegisterUserResult(UserId userId, String name, Email email, UserStatus status) {
}