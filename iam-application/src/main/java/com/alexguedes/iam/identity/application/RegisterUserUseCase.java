package com.alexguedes.iam.identity.application;

import com.alexguedes.iam.identity.domain.Email;
import com.alexguedes.iam.identity.domain.PasswordHash;
import com.alexguedes.iam.identity.domain.PasswordHasher;
import com.alexguedes.iam.identity.domain.User;
import com.alexguedes.iam.identity.domain.UserId;
import com.alexguedes.iam.identity.domain.UserRepository;
import com.alexguedes.iam.identity.domain.exception.UserAlreadyExistsException;

import java.util.Objects;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = Objects.requireNonNull(
                userRepository,
                "User repository must not be null"
        );

        this.passwordHasher = Objects.requireNonNull(
                passwordHasher,
                "Password hasher must not be null"
        );
    }

    public RegisterUserResult execute(RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Register user command must not be null");
        }

        Email email = new Email(command.email());
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        PasswordHash passwordHash = passwordHasher.hash(command.password());
        User user = new User(UserId.newId(), command.name(), email, passwordHash);
        User savedUser = userRepository.save(user);

        return new RegisterUserResult(savedUser.id(), savedUser.name(), savedUser.email(), savedUser.status());
    }
}