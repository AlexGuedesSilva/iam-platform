package com.alexguedes.iam.identity.infrastructure.config;

import com.alexguedes.iam.identity.application.RegisterUserUseCase;
import com.alexguedes.iam.identity.domain.port.PasswordHasher;
import com.alexguedes.iam.identity.domain.port.UserIdGenerator;
import com.alexguedes.iam.identity.domain.port.UserRepository;
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
}
