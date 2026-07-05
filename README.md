# IAM Platform

![IAM Platform Architecture](docs/diagrams/iam-platform-architecture-dark.png)

Building an Identity and Access Management Platform using **Java**, **Spring Boot**, **Domain-Driven Design** and **Clean Architecture**.

---

## Project Goal

IAM Platform is a Java Spring Boot platform for identity and access management.

The goal is to provide a clear foundation for authentication, authorization, user lifecycle, access policies, and security-related capabilities while keeping the system modular, understandable, and ready to evolve.

The project currently focuses on the **Identity module**, including the core domain model, user registration use case, persistence adapter, database migration, REST API, validation, exception handling, endpoint documentation, and security foundation.

---

## Architecture Overview

The platform is organized as a Maven multi-module project with a single deployable direction and clearly separated responsibilities.

The architecture favors explicit module boundaries, shared language, and isolated application concerns so the codebase can grow without becoming tightly coupled.

The initial structure separates edge concerns, IAM application concerns, and shared cross-module foundations. Documentation and infrastructure folders are included from the beginning to keep architecture decisions, diagrams, and deployment assets close to the codebase.

---

## Modular Monolith Approach

IAM Platform starts as a **Modular Monolith**.

This keeps development and deployment simple while still enforcing boundaries between major parts of the system.

The intent is to gain the operational simplicity of a monolith without treating the codebase as one large undifferentiated application. Each module should own a clear responsibility, communicate through deliberate contracts, and avoid leaking internal details into other modules.

This approach allows the platform to evolve gradually. If a future need justifies extracting a module into a separate service, the codebase should already have the conceptual boundaries needed to make that move with less friction.

---

## Clean Architecture + DDD

The project follows **Clean Architecture** and **Domain-Driven Design** principles as architectural guidance.

Clean Architecture provides the direction for dependency flow: core business concepts should remain independent of frameworks, transport mechanisms, databases, and infrastructure details.

Domain-Driven Design provides the modeling discipline: the platform is organized around the IAM domain language, with clear boundaries, meaningful concepts, and modules that reflect business capabilities rather than technical layers alone.

The current implementation keeps the Identity domain and application layers protected from REST, persistence, and security framework details.

External concerns such as HTTP controllers, exception handling, database adapters, Flyway migrations, Spring Security configuration, and BCrypt password hashing are implemented at the edges of the system.

---

## Identity Module

The Identity module currently includes:

- User domain model
- UserId value object
- Email value object
- PasswordHash value object
- User status
- Domain exceptions
- Register user use case
- Application ports
- User repository port
- User ID generator port
- Password hasher port
- JPA persistence adapter
- Flyway database migration
- REST API for user registration
- Validation and exception handling
- Security foundation with BCrypt password hashing

The module follows the dependency direction:

```text
Domain
  ↑
Application
  ↑
Infrastructure / Interfaces
```

Frameworks and external tools depend on the core. The core does not depend on frameworks.

---

## Security Foundation

The project includes an initial security foundation using Spring Security.

Current security capabilities:

- Basic Spring Security configuration
- Stateless session policy
- CSRF disabled for REST API usage
- BCrypt `PasswordEncoder` bean
- `PasswordHasher` application port
- `BCryptPasswordHasher` infrastructure implementation
- Unit tests validating BCrypt hashing behavior

The use case depends on the `PasswordHasher` abstraction, not directly on BCrypt or Spring Security.

```text
RegisterUserUseCase
↓
PasswordHasher
↓
BCryptPasswordHasher
↓
BCryptPasswordEncoder
```

This keeps the application layer independent of the hashing algorithm and framework-specific implementation.

---

## Current Status

| Area                                 | Status    |
|--------------------------------------|-----------|
| Project foundation                   | ✅ Done    |
| Identity domain model                | ✅ Done    |
| Register user use case               | ✅ Done    |
| Domain exceptions                    | ✅ Done    |
| Persistence adapter                  | ✅ Done    |
| Database migration                   | ✅ Done    |
| Identity REST API                    | ✅ Done    |
| Register user endpoint documentation | ✅ Done    |
| Security foundation                  | ✅ Done    |
| BCrypt password hashing              | ✅ Done    |
| Authentication                       | ⚪ Planned |
| Authorization                        | ⚪ Planned |
| Sessions                             | ⚪ Planned |
| API Keys                             | ⚪ Planned |
| Audit                                | ⚪ Planned |
| OAuth2 / OIDC                        | ⚪ Planned |

---

## Main Modules

- `iam-application`:

Main IAM application module and primary place for identity and access management capabilities.

Contains the Identity domain, use cases, ports, infrastructure adapters, REST interfaces, persistence, migrations, and security foundation.

- `api-gateway`:

Gateway module responsible for edge-facing concerns and platform entry points.

- `shared-kernel`:

Shared foundation for stable cross-module concepts that are intentionally reused.

This module should remain small and should not contain application-specific infrastructure configuration.

---

## API Documentation

- [Identity user registration endpoint](docs/identity/register-user-endpoint.md)

---

## Project Layout

```text
iam-platform/
├── iam-application/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/alexguedes/iam/identity/
│       │   │       ├── application/
│       │   │       ├── domain/
│       │   │       ├── infrastructure/
│       │   │       └── interfaces/
│       │   └── resources/
│       │       └── db/migration/
│       └── test/
├── api-gateway/
├── shared-kernel/
├── docs/
│   ├── architecture/
│   ├── identity/
│   │   └── register-user-endpoint.md
│   └── diagrams/
│       └── iam-platform-architecture-dark.png
├── docker/
├── pom.xml
└── README.md
```

---

## Test Coverage

This project uses JaCoCo to generate test coverage reports.

Run:

```bash
mvn clean verify
```
Run all tests:

```bash
mvn clean test
```

---
## Technology Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- Testcontainers
- JaCoCo

---

## Roadmap

Next planned capabilities:

- Hash password during user registration flow
- Authentication use case
- Login endpoint
- JWT access token
- Refresh token and session control
- Authorization with roles and permissions
- API key management
- Audit events
- OAuth2 / OpenID Connect integration

