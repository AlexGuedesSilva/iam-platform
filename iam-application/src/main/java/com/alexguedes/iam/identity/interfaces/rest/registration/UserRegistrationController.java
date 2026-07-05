package com.alexguedes.iam.identity.interfaces.rest.registration;

import com.alexguedes.iam.identity.application.usecase.RegisterUserResult;
import com.alexguedes.iam.identity.application.usecase.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity/users")
public class UserRegistrationController {

    private final RegisterUserUseCase registerUserUseCase;

    public UserRegistrationController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserResult result = registerUserUseCase.execute(request.toCommand());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RegisterUserResponse.from(result));
    }
}
