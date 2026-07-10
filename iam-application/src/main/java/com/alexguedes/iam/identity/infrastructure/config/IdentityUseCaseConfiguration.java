package com.alexguedes.iam.identity.infrastructure.config;

import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserUseCase;
import com.alexguedes.iam.identity.application.usecase.registration.RegisterUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityUseCaseConfiguration {

    @Bean
    RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            UserIdGenerator userIdGenerator
    ) {
        return new RegisterUserUseCase(userRepository, passwordHasher, userIdGenerator);
    }

    @Bean
    LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenIssuer accessTokenIssuer
    ) {
        return new LoginUserUseCase(userRepository, passwordHasher, accessTokenIssuer);
    }
}
