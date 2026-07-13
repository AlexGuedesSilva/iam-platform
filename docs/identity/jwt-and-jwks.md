# JWT and JWKS

## JWT issuance

`Rs256JwtAccessTokenIssuer` uses the configured PKCS#8 RSA private key to sign access tokens with RS256. The JWT header includes `alg=RS256` and the configured `kid`.

Current claims:

| Claim | Value |
|---|---|
| `sub` | UUID user ID |
| `iss` | `iam-platform` |
| `iat` | Issuance instant |
| `exp` | Issuance plus 15 minutes |
| `email` | User email |
| `status` | User status |
| `token_type` | `access` |

## JWKS publication

`Rs256JwkProvider` converts the configured X.509 RSA public key to a JWK. `JwksController` publishes it at `GET /.well-known/jwks.json`. Consumers use the JWT header `kid` to choose the matching key and verify the signature.

The private key signs; only the public key is published. Neither real keys nor tokens belong in documentation, logs, or version control.

## Not implemented

Refresh tokens, token revocation, key rotation, resource-server validation, RBAC, OAuth 2.0, and OpenID Connect are not part of the current implementation.

See the [JWT/JWKS diagram](../diagrams/README.md#jwt-and-jwks-flow) and [local development](../local-development.md) for key generation.
