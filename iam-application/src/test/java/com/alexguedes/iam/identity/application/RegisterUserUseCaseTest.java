package com.alexguedes.iam.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.domain.Email;
import com.alexguedes.iam.identity.domain.PasswordHash;
import com.alexguedes.iam.identity.domain.PasswordHasher;
import com.alexguedes.iam.identity.domain.User;
import com.alexguedes.iam.identity.domain.UserId;
import com.alexguedes.iam.identity.domain.UserRepository;
import com.alexguedes.iam.identity.domain.UserStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RegisterUserUseCaseTest {

    private static final String VALID_NAME = "Alex Guedes";
    private static final String VALID_EMAIL = "alex@example.com";
    private static final String VALID_PASSWORD = "raw-password";
    private static final String HASHED_PASSWORD = "12345678901234567890123456789012";

    @Test
    void shouldRegisterNewUserSuccessfully() {
        FakeUserRepository userRepository = new FakeUserRepository();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher);
        RegisterUserCommand command = new RegisterUserCommand(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);

        RegisterUserResult result = useCase.execute(command);

        assertTrue(passwordHasher.wasCalled);
        assertEquals(VALID_PASSWORD, passwordHasher.rawPassword);
        assertTrue(userRepository.wasSaved);
        assertNotNull(userRepository.savedUser);
        assertEquals(UserStatus.ACTIVE, userRepository.savedUser.status());
        assertEquals(new Email(VALID_EMAIL), userRepository.savedUser.email());
        assertEquals(new PasswordHash(HASHED_PASSWORD), userRepository.savedUser.passwordHash());
        assertNotNull(result.userId());
        assertEquals(userRepository.savedUser.id(), result.userId());
        assertEquals(new Email(VALID_EMAIL), result.email());
        assertEquals(UserStatus.ACTIVE, result.status());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        FakeUserRepository userRepository = new FakeUserRepository();
        userRepository.alreadyRegisteredEmail = new Email(VALID_EMAIL);
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher);
        RegisterUserCommand command = new RegisterUserCommand(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);

        assertThrows(IllegalStateException.class, () -> useCase.execute(command));

        assertFalse(passwordHasher.wasCalled);
        assertFalse(userRepository.wasSaved);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new RegisterUserCommand(name, VALID_EMAIL, VALID_PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidEmail(String email) {
        assertThrows(IllegalArgumentException.class, () -> new RegisterUserCommand(VALID_NAME, email, VALID_PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidPassword(String password) {
        assertThrows(IllegalArgumentException.class, () -> new RegisterUserCommand(VALID_NAME, VALID_EMAIL, password));
    }

    private static final class FakeUserRepository implements UserRepository {

        private final Map<UserId, User> usersById = new HashMap<>();
        private Email alreadyRegisteredEmail;
        private boolean wasSaved;
        private User savedUser;

        @Override
        public User save(User user) {
            wasSaved = true;
            savedUser = user;
            usersById.put(user.id(), user);
            return user;
        }

        @Override
        public Optional<User> findById(UserId id) {
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            if (alreadyRegisteredEmail != null && alreadyRegisteredEmail.equals(email)) {
                return Optional.of(new User(UserId.newId(), "Registered User", email, new PasswordHash(HASHED_PASSWORD)));
            }
            return Optional.empty();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return alreadyRegisteredEmail != null && alreadyRegisteredEmail.equals(email);
        }
    }

    private static final class FakePasswordHasher implements PasswordHasher {

        private boolean wasCalled;
        private String rawPassword;

        @Override
        public PasswordHash hash(String rawPassword) {
            wasCalled = true;
            this.rawPassword = rawPassword;
            return new PasswordHash(HASHED_PASSWORD);
        }
    }
}