# Login flow

`POST /auth/login` authenticates an existing user and issues an access token.

## Sequence

1. `LoginController` validates JSON and creates `LoginUserCommand`.
2. `LoginUserUseCase` normalizes the email and loads the user through `UserRepository`.
3. `PasswordHasher.matches` verifies the raw password against `PasswordHash`.
4. The use case creates claims with user identity, issuer, timestamps, status, and token type.
5. `AccessTokenIssuer` returns an RS256-signed token and its expiration.
6. The controller maps `LoginUserResult` to `LoginResponse` and returns `200 OK`.

Unknown email and incorrect password both produce the same `401 Unauthorized` authentication failure. This avoids exposing which credential was incorrect.

## Authentication result

The response contains `userId`, `email`, `status`, `accessToken`, `tokenType`, and `expiresAt`. The current token type is `Bearer`, and expiration is 15 minutes after issuance.

See [security](security.md), [JWT and JWKS](jwt-and-jwks.md), and the [login diagram](../diagrams/README.md#login-flow).
