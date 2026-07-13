# Security architecture

The current security path covers credential protection, authentication, access-token issuance, and publication of verification keys.

## Credentials

Registration sends the raw password to `PasswordHasher`. `BCryptPasswordHasher` returns a `PasswordHash`, and only that hash is stored. Login compares the submitted password with the stored hash. Raw passwords are never persisted.

## Access tokens

After successful credential verification, `LoginUserUseCase` builds `AccessTokenClaims` and calls `AccessTokenIssuer`. `Rs256JwtAccessTokenIssuer` signs a JWT with the configured RSA private key and sets the configured `kid` in its header. Tokens currently expire 15 minutes after issuance and use issuer `iam-platform`.

## Public verification material

`Rs256JwkProvider` derives a JWK representation from the configured RSA public key. `GET /.well-known/jwks.json` exposes only public material. The `kid` lets a verifier select the JWK matching the token header.

Private keys must remain outside source control. JWKS must never expose a private key.

## Current boundary

Implemented: BCrypt, credential verification, RS256 signing, JWT claims, expiration, `kid`, and JWKS.

Planned: refresh tokens, revocation, signing-key rotation, RBAC, OAuth 2.0, OpenID Connect, and resource-server JWT validation.

See [JWT and JWKS](../identity/jwt-and-jwks.md) and the [security diagrams](../diagrams/README.md#jwt-and-jwks-flow).
