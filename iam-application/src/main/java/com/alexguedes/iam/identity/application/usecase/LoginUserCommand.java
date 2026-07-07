package com.alexguedes.iam.identity.application.usecase;

import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;

public record LoginUserCommand(String email, String password) {

    public LoginUserCommand {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank");
        }
    }
}
