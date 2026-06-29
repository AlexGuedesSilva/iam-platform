package com.alexguedes.iam.identity.interfaces.rest;

import com.alexguedes.iam.identity.application.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Password must not be blank")
        String password
) {

    RegisterUserCommand toCommand() {
        return new RegisterUserCommand(name, email, password);
    }
}
