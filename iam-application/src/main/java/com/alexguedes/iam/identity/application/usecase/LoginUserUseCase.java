package com.alexguedes.iam.identity.application.usecase;

import com.alexguedes.iam.identity.application.exception.AuthenticationFailedException;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.Email;

import java.util.Objects;

public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = Objects.requireNonNull(
                userRepository,
                "User repository must not be null"
        );

        this.passwordHasher = Objects.requireNonNull(
                passwordHasher,
                "Password hasher must not be null"
        );
    }

    public LoginUserResult execute(LoginUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Login user command must not be null");
        }

        Email email = new Email(command.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticationFailedException::new);

        if (!passwordHasher.matches(command.password(), user.passwordHash())) {
            throw new AuthenticationFailedException();
        }

        return new LoginUserResult(
                user.id(),
                user.name(),
                user.email(),
                user.status(),
                user.createdAt()
        );
    }
}
