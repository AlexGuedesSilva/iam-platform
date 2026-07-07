package com.alexguedes.iam.identity.interfaces.rest.authentication;

import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Password must not be blank")
        String password
) {

    LoginUserCommand toCommand() {
        return new LoginUserCommand(email, password);
    }
}
