# Identity domain model

## User

`User` is the aggregate root for the current Identity capability. It owns its identifier, name, email, password hash, status, and timestamps. Construction and behavior enforce invariants; public setters are intentionally absent.

`UserStatus` supports `ACTIVE`, `INACTIVE`, and `BLOCKED`. The database migration constrains persisted values to the same set.

## Value objects

- `UserId` wraps a non-null UUID.
- `Email` validates and normalizes identity email data.
- `PasswordHash` represents an already-protected password, not a raw credential.

Value objects make invalid or ambiguous primitive values harder to pass through the core.

## Exceptions

Domain exceptions communicate invalid email, password, or user-name data, duplicate users, missing users, and invalid status transitions. Authentication failure is an application exception so login does not reveal whether the email or password was wrong.

## Persistence independence

`User` is not a JPA entity. `UserJpaEntity` is a separate infrastructure representation, connected by `UserPersistenceMapper`. See [persistence](persistence.md).
