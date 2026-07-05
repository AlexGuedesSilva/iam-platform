package com.alexguedes.iam.identity.application.usecase;

import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;
import com.alexguedes.iam.identity.domain.exception.InvalidUserNameException;

public record RegisterUserCommand(String name, String email, String password) {

    public RegisterUserCommand {
        if (name == null || name.isBlank()) {
            throw new InvalidUserNameException("Name must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank");
        }
    }
}