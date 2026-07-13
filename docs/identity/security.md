# Identity security

## Password protection

`BCryptPasswordHasher` implements the application `PasswordHasher` port. Registration persists only the BCrypt result represented by `PasswordHash`; raw passwords are not stored. Login verifies the supplied password through the same abstraction.

## User identifiers

`UuidUserIdGenerator` implements `UserIdGenerator` and creates a new UUID for each registered identity. ID generation remains outside `RegisterUserUseCase` and is replaceable through the port.

## Authentication and tokens

Credential verification occurs before token issuance. Successful login delegates to `Rs256JwtAccessTokenIssuer`; failed authentication returns 401 without distinguishing a missing user from an incorrect password.

Refresh tokens, token revocation, RBAC, and resource-server validation are not implemented. OAuth 2.0 and OpenID Connect are planned.

See [login flow](login-flow.md), [JWT and JWKS](jwt-and-jwks.md), and [security architecture](../architecture/security-architecture.md).
