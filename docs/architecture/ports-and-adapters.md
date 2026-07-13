# Ports and Adapters

Ports describe what the core needs. Adapters connect those needs to concrete technologies.

| Port | Owner | Current adapter | Responsibility |
|---|---|---|---|
| `UserRepository` | Domain | `JpaUserRepositoryAdapter` | Find and persist users without exposing JPA to the core. |
| `PasswordHasher` | Application | `BCryptPasswordHasher` | Hash raw passwords and verify credentials. |
| `UserIdGenerator` | Application | `UuidUserIdGenerator` | Create unique `UserId` values. |
| `AccessTokenIssuer` | Application | `Rs256JwtAccessTokenIssuer` | Convert application claims into signed access tokens. |

`Rs256JwkProvider` is an infrastructure service used by the JWKS REST adapter to publish public verification material. `SpringDataUserRepository`, `UserJpaEntity`, and `UserPersistenceMapper` support the JPA repository adapter.

Spring configuration composes these objects. No Spring stereotype is required in the domain or application layers.

## Example dependency inversion

`LoginUserUseCase` depends on `AccessTokenIssuer`. The RS256 implementation depends on Nimbus JOSE + JWT and RSA keys, but those details do not enter the use case. A different token format could be introduced through another adapter without changing authentication rules.

Related documents: [layers](layers.md), [security architecture](security-architecture.md), and [identity persistence](../identity/persistence.md).
