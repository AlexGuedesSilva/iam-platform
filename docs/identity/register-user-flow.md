# User registration flow

`POST /identity/users/register` creates an active user through `RegisterUserUseCase`.

## Sequence

1. `UserRegistrationController` validates JSON and creates `RegisterUserCommand`.
2. The use case creates domain values and checks `UserRepository` for an existing email.
3. `PasswordHasher` converts the raw password to a BCrypt-backed `PasswordHash`.
4. `UserIdGenerator` produces a UUID-backed `UserId`.
5. The domain `User` is created with its initial state.
6. `UserRepository` persists the user through the JPA adapter.
7. The use case returns `RegisterUserResult`; the controller returns `201 Created`.

Duplicate email produces `409 Conflict`. Invalid input produces `400 Bad Request`; malformed JSON also produces 400. Unsupported content types produce `415 Unsupported Media Type`.

The controller does not generate IDs, hash passwords, or access JPA directly. See the [registration diagram](../diagrams/README.md#registration-flow) and [REST API](rest-api.md).
