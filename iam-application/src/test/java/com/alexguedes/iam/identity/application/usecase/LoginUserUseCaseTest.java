package com.alexguedes.iam.identity.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.application.exception.AuthenticationFailedException;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.application.port.security.AccessTokenClaims;
import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.application.port.security.IssuedAccessToken;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserCommand;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserResult;
import com.alexguedes.iam.identity.application.usecase.authentication.LoginUserUseCase;
import com.alexguedes.iam.identity.domain.exception.InvalidEmailException;
import com.alexguedes.iam.identity.domain.exception.InvalidPasswordException;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LoginUserUseCaseTest {

    private static final String VALID_NAME = "Alex Guedes";
    private static final String VALID_EMAIL = "alex@example.com";
    private static final String VALID_PASSWORD = "raw-password";
    private static final String WRONG_PASSWORD = "wrong-password";
    private static final String HASHED_PASSWORD = "12345678901234567890123456789012";
    private static final String ACCESS_TOKEN = "signed-access-token";
    private static final String TOKEN_TYPE = "Bearer";
    private static final UserId USER_ID = new UserId(UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd"));

    @Test
    void shouldLoginUserSuccessfully() {
        FakeUserRepository userRepository = new FakeUserRepository();
        User user = new User(USER_ID, VALID_NAME, new Email(VALID_EMAIL), new PasswordHash(HASHED_PASSWORD));
        userRepository.save(user);
        FakePasswordHasher passwordHasher = new FakePasswordHasher(true);
        FakeAccessTokenIssuer accessTokenIssuer = new FakeAccessTokenIssuer();
        LoginUserUseCase useCase = new LoginUserUseCase(userRepository, passwordHasher, accessTokenIssuer);
        LoginUserCommand command = new LoginUserCommand(VALID_EMAIL, VALID_PASSWORD);

        Instant beforeLogin = Instant.now();
        LoginUserResult result = useCase.execute(command);
        Instant afterLogin = Instant.now();

        assertTrue(passwordHasher.wasCalled);
        assertEquals(VALID_PASSWORD, passwordHasher.rawPassword);
        assertEquals(user.passwordHash(), passwordHasher.passwordHash);
        assertNotNull(accessTokenIssuer.claims);
        assertEquals(USER_ID, accessTokenIssuer.claims.subject());
        assertEquals(new Email(VALID_EMAIL), accessTokenIssuer.claims.email());
        assertEquals(UserStatus.ACTIVE, accessTokenIssuer.claims.status());
        assertEquals("iam-platform", accessTokenIssuer.claims.issuer());
        assertFalse(accessTokenIssuer.claims.issuedAt().isBefore(beforeLogin));
        assertFalse(accessTokenIssuer.claims.issuedAt().isAfter(afterLogin));
        assertEquals(Duration.ofMinutes(15), Duration.between(
                accessTokenIssuer.claims.issuedAt(),
                accessTokenIssuer.claims.expiresAt()
        ));
        assertEquals("access", accessTokenIssuer.claims.tokenType());
        assertEquals(USER_ID, result.userId());
        assertEquals(VALID_NAME, result.name());
        assertEquals(new Email(VALID_EMAIL), result.email());
        assertEquals(UserStatus.ACTIVE, result.status());
        assertEquals(user.createdAt(), result.createdAt());
        assertNotNull(result.createdAt());
        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(TOKEN_TYPE, result.tokenType());
        assertEquals(accessTokenIssuer.claims.expiresAt(), result.expiresAt());
    }

    @Test
    void shouldRejectUnknownUserWithAuthenticationException() {
        FakeUserRepository userRepository = new FakeUserRepository();
        FakePasswordHasher passwordHasher = new FakePasswordHasher(true);
        FakeAccessTokenIssuer accessTokenIssuer = new FakeAccessTokenIssuer();
        LoginUserUseCase useCase = new LoginUserUseCase(userRepository, passwordHasher, accessTokenIssuer);
        LoginUserCommand command = new LoginUserCommand(VALID_EMAIL, VALID_PASSWORD);

        assertThrows(AuthenticationFailedException.class, () -> useCase.execute(command));

        assertFalse(passwordHasher.wasCalled);
        assertFalse(accessTokenIssuer.wasCalled);
    }

    @Test
    void shouldRejectInvalidPasswordWithAuthenticationException() {
        FakeUserRepository userRepository = new FakeUserRepository();
        User user = new User(USER_ID, VALID_NAME, new Email(VALID_EMAIL), new PasswordHash(HASHED_PASSWORD));
        userRepository.save(user);
        FakePasswordHasher passwordHasher = new FakePasswordHasher(false);
        FakeAccessTokenIssuer accessTokenIssuer = new FakeAccessTokenIssuer();
        LoginUserUseCase useCase = new LoginUserUseCase(userRepository, passwordHasher, accessTokenIssuer);
        LoginUserCommand command = new LoginUserCommand(VALID_EMAIL, WRONG_PASSWORD);

        assertThrows(AuthenticationFailedException.class, () -> useCase.execute(command));

        assertTrue(passwordHasher.wasCalled);
        assertEquals(WRONG_PASSWORD, passwordHasher.rawPassword);
        assertEquals(user.passwordHash(), passwordHasher.passwordHash);
        assertFalse(accessTokenIssuer.wasCalled);
    }

    @Test
    void shouldRejectNullCommand() {
        FakeUserRepository userRepository = new FakeUserRepository();
        FakePasswordHasher passwordHasher = new FakePasswordHasher(true);
        LoginUserUseCase useCase = new LoginUserUseCase(
                userRepository,
                passwordHasher,
                new FakeAccessTokenIssuer()
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectBlankEmail(String email) {
        assertThrows(InvalidEmailException.class, () -> new LoginUserCommand(email, VALID_PASSWORD));
    }

    @Test
    void shouldReuseEmailValidation() {
        assertThrows(InvalidEmailException.class, () -> new LoginUserUseCase(
                new FakeUserRepository(),
                new FakePasswordHasher(true),
                new FakeAccessTokenIssuer()
        ).execute(new LoginUserCommand("invalid-email", VALID_PASSWORD)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectBlankPassword(String password) {
        assertThrows(InvalidPasswordException.class, () -> new LoginUserCommand(VALID_EMAIL, password));
    }

    private static final class FakeUserRepository implements UserRepository {

        private final Map<UserId, User> usersById = new HashMap<>();
        private final Map<Email, User> usersByEmail = new HashMap<>();

        @Override
        public User save(User user) {
            usersById.put(user.id(), user);
            usersByEmail.put(user.email(), user);
            return user;
        }

        @Override
        public Optional<User> findById(UserId id) {
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public boolean existsByEmail(Email email) {
            return usersByEmail.containsKey(email);
        }
    }

    private static final class FakePasswordHasher implements PasswordHasher {

        private final boolean matches;
        private boolean wasCalled;
        private String rawPassword;
        private PasswordHash passwordHash;

        private FakePasswordHasher(boolean matches) {
            this.matches = matches;
        }

        @Override
        public PasswordHash hash(String rawPassword) {
            return new PasswordHash(HASHED_PASSWORD);
        }

        @Override
        public boolean matches(String rawPassword, PasswordHash passwordHash) {
            wasCalled = true;
            this.rawPassword = rawPassword;
            this.passwordHash = passwordHash;
            return matches;
        }
    }

    private static final class FakeAccessTokenIssuer implements AccessTokenIssuer {

        private boolean wasCalled;
        private AccessTokenClaims claims;

        @Override
        public IssuedAccessToken issue(AccessTokenClaims claims) {
            wasCalled = true;
            this.claims = claims;
            return new IssuedAccessToken(ACCESS_TOKEN, TOKEN_TYPE, claims.expiresAt());
        }
    }
}
