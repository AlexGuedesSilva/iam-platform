# Clean Architecture layers

## Domain

`identity.domain` owns the core model: `User`, `UserStatus`, value objects, repository contract, and domain exceptions. The model protects its invariants and exposes behavior without public setters. It has no Spring or JPA annotations.

## Application

`identity.application` coordinates business workflows. `RegisterUserUseCase` and `LoginUserUseCase` accept commands, use domain types, call ports, and return results. This layer defines `PasswordHasher`, `UserIdGenerator`, and `AccessTokenIssuer`; it does not know their implementations.

## Infrastructure

`identity.infrastructure` implements technical details:

- JPA and PostgreSQL persistence
- BCrypt password hashing
- UUID user-ID generation
- Nimbus-based RS256 JWT signing and JWK construction
- Spring bean and security configuration

## Interfaces / REST

`identity.interfaces.rest` owns controllers, HTTP request/response records, Jakarta Bean Validation, and exception-to-HTTP mapping. Controllers translate transport data to application commands and do not contain domain rules.

## Rules

1. Domain code must remain framework-independent.
2. Application code depends on abstractions, not adapters.
3. Infrastructure may depend inward to implement ports.
4. REST may invoke application use cases but must not bypass them to access persistence.
5. Wiring belongs in infrastructure configuration.

See the [package dependency diagram](../diagrams/README.md#package-and-layer-dependencies).
