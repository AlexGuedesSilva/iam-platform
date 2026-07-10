package com.alexguedes.iam.identity.application.usecase.authentication;

import com.alexguedes.iam.identity.application.exception.AuthenticationFailedException;
import com.alexguedes.iam.identity.application.port.out.UserRepository;
import com.alexguedes.iam.identity.application.port.security.AccessTokenClaims;
import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.application.port.security.IssuedAccessToken;
import com.alexguedes.iam.identity.application.port.security.PasswordHasher;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.Email;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class LoginUserUseCase {

    private static final String ACCESS_TOKEN_ISSUER = "iam-platform";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenIssuer accessTokenIssuer;

    public LoginUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            AccessTokenIssuer accessTokenIssuer
    ) {
        this.userRepository = Objects.requireNonNull(
                userRepository,
                "User repository must not be null"
        );

        this.passwordHasher = Objects.requireNonNull(
                passwordHasher,
                "Password hasher must not be null"
        );

        this.accessTokenIssuer = Objects.requireNonNull(
                accessTokenIssuer,
                "Access token issuer must not be null"
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

        Instant issuedAt = Instant.now();
        AccessTokenClaims claims = new AccessTokenClaims(
                user.id(),
                user.email(),
                user.status(),
                ACCESS_TOKEN_ISSUER,
                issuedAt,
                issuedAt.plus(15, ChronoUnit.MINUTES),
                ACCESS_TOKEN_TYPE
        );
        IssuedAccessToken issuedAccessToken = accessTokenIssuer.issue(claims);

        return new LoginUserResult(
                user.id(),
                user.name(),
                user.email(),
                user.status(),
                user.createdAt(),
                issuedAccessToken.value(),
                issuedAccessToken.tokenType(),
                issuedAccessToken.expiresAt()
        );
    }
}
