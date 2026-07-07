package com.alexguedes.iam.identity.application.usecase.registration;

import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.domain.exception.UserAlreadyExistsException;

import java.util.Objects;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserIdGenerator userIdGenerator;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher, UserIdGenerator userIdGenerator) {
        this.userRepository = Objects.requireNonNull(
                userRepository,
                "User repository must not be null"
        );

        this.passwordHasher = Objects.requireNonNull(
                passwordHasher,
                "Password hasher must not be null"
        );

        this.userIdGenerator = Objects.requireNonNull(
                userIdGenerator,
                "User id generator must not be null"
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
        UserId userId = userIdGenerator.generate();
        User user = new User(userId, command.name(), email, passwordHash);
        User savedUser = userRepository.save(user);

        return new RegisterUserResult(
                savedUser.id(),
                savedUser.name(),
                savedUser.email(),
                savedUser.status(),
                savedUser.createdAt()
        );
    }
}
