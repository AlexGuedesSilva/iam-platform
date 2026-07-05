package com.alexguedes.iam.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.application.usecase.RegisterUserCommand;
import com.alexguedes.iam.identity.application.usecase.RegisterUserResult;
import com.alexguedes.iam.identity.application.usecase.RegisterUserUseCase;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;
import com.alexguedes.iam.identity.domain.exception.InvalidUserNameException;
import com.alexguedes.iam.identity.domain.exception.UserAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RegisterUserUseCaseTest {

    private static final String VALID_NAME = "Alex Guedes";
    private static final String VALID_EMAIL = "alex@example.com";
    private static final String VALID_PASSWORD = "raw-password";
    private static final String HASHED_PASSWORD = "12345678901234567890123456789012";
    private static final UserId GENERATED_USER_ID = new UserId(UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd"));

    @Test
    void shouldRegisterNewUserSuccessfully() {
        FakeUserRepository userRepository = new FakeUserRepository();
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeUserIdGenerator userIdGenerator = new FakeUserIdGenerator(GENERATED_USER_ID);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher, userIdGenerator);
        RegisterUserCommand command = new RegisterUserCommand(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);

        RegisterUserResult result = useCase.execute(command);

        assertTrue(userIdGenerator.wasCalled);
        assertTrue(passwordHasher.wasCalled);
        assertEquals(VALID_PASSWORD, passwordHasher.rawPassword);
        assertTrue(userRepository.wasSaved);
        assertNotNull(userRepository.savedUser);
        assertEquals(GENERATED_USER_ID, userRepository.savedUser.id());
        assertEquals(UserStatus.ACTIVE, userRepository.savedUser.status());
        assertEquals(new Email(VALID_EMAIL), userRepository.savedUser.email());
        assertEquals(new PasswordHash(HASHED_PASSWORD), userRepository.savedUser.passwordHash());
        assertNotNull(result.userId());
        assertEquals(userRepository.savedUser.id(), result.userId());
        assertEquals(new Email(VALID_EMAIL), result.email());
        assertEquals(UserStatus.ACTIVE, result.status());
        assertEquals(userRepository.savedUser.createdAt(), result.createdAt());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        FakeUserRepository userRepository = new FakeUserRepository();
        userRepository.alreadyRegisteredEmail = new Email(VALID_EMAIL);
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeUserIdGenerator userIdGenerator = new FakeUserIdGenerator(GENERATED_USER_ID);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher, userIdGenerator);
        RegisterUserCommand command = new RegisterUserCommand(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);

        assertThrows(UserAlreadyExistsException.class, () -> useCase.execute(command));

        assertFalse(userIdGenerator.wasCalled);
        assertFalse(passwordHasher.wasCalled);
        assertFalse(userRepository.wasSaved);
    }

    @Test
    void shouldRejectNullCommand() {
        FakeUserRepository userRepository = new FakeUserRepository();
        userRepository.alreadyRegisteredEmail = new Email(VALID_EMAIL);
        FakePasswordHasher passwordHasher = new FakePasswordHasher();
        FakeUserIdGenerator userIdGenerator = new FakeUserIdGenerator(GENERATED_USER_ID);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, passwordHasher, userIdGenerator);
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidName(String name) {
        assertThrows(InvalidUserNameException.class, () -> new RegisterUserCommand(name, VALID_EMAIL, VALID_PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidEmail(String email) {
        assertThrows(InvalidEmailException.class, () -> new RegisterUserCommand(VALID_NAME, email, VALID_PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidPassword(String password) {
        assertThrows(InvalidPasswordException.class, () -> new RegisterUserCommand(VALID_NAME, VALID_EMAIL, password));
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
                return Optional.of(new User(GENERATED_USER_ID, "Registered User", email, new PasswordHash(HASHED_PASSWORD)));
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

        @Override
        public boolean matches(String rawPassword, PasswordHash passwordHash) {
            return passwordHash.value().equals(HASHED_PASSWORD);
        }
    }

    private static final class FakeUserIdGenerator implements UserIdGenerator {

        private final UserId userId;
        private boolean wasCalled;

        private FakeUserIdGenerator(UserId userId) {
            this.userId = userId;
        }

        @Override
        public UserId generate() {
            wasCalled = true;
            return userId;
        }
    }
}
