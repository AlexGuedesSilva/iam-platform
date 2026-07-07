package com.alexguedes.iam.identity.interfaces.rest.authentication;

import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserResult;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginUserUseCase loginUserUseCase;

    public LoginController(LoginUserUseCase loginUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUserResult result = loginUserUseCase.execute(request.toCommand());
        return ResponseEntity.ok(LoginResponse.from(result));
    }
}
