# Architecture overview

IAM Platform is a Java 21 and Spring Boot 3.5 Maven multi-module project. It combines Domain-Driven Design, Clean Architecture, Ports and Adapters, dependency inversion, and a modular monolith deployment model.

The current executable is `iam-application`. It contains the implemented Identity capability: registration, login, PostgreSQL persistence, BCrypt password protection, UUID generation, RS256 access-token issuance, and JWKS publication.

## Architectural goals

- Keep business rules independent of Spring, HTTP, JPA, databases, and cryptographic libraries.
- Express identity concepts through a rich domain model and value objects.
- Make external capabilities replaceable through ports.
- Keep one simple deployment while maintaining explicit internal boundaries.
- Test domain and application behavior without starting infrastructure.

## Dependency direction

Dependencies point toward the core:

```text
interfaces/rest ──> application ──> domain
infrastructure ──> application and domain abstractions
```

The domain does not depend on application or infrastructure. Application use cases depend on domain types and ports. Spring configuration creates the concrete graph at the edge.

See [layers](layers.md), [ports and adapters](ports-and-adapters.md), and the [diagrams](../diagrams/README.md).

## Current and planned scope

Registration, login, JWT issuance, and JWKS publication are implemented. Refresh tokens, revocation, RBAC, OAuth 2.0, OpenID Connect, and resource-server validation are planned, not implemented.

The modular monolith may evolve into services only when operational or scaling requirements justify extraction. Existing module and port boundaries are intended to make that evolution deliberate rather than premature.
