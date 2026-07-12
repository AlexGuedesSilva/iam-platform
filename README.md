# IAM Platform

![IAM Platform architecture](docs/diagrams/iam-platform-architecture-dark.png)

IAM Platform is a personal learning and engineering project focused on building an Identity and Access Management foundation with Java 21 and Spring Boot 3.5. It applies enterprise architecture practices to secure identity management, authentication, access-token issuance, and the foundation for future authorization capabilities.

The project follows Domain-Driven Design (DDD), Clean Architecture, Ports and Adapters, and a modular monolith approach. It is intentionally designed so business rules remain independent of frameworks and external technologies.

## Current status

The current version is functional and can be started locally. It connects to PostgreSQL, applies Flyway migrations, registers users, authenticates credentials, issues RS256-signed JWT access tokens, and publishes the corresponding public key through JWKS.

| Capability | Status |
|---|---|
| Spring Boot application startup | Implemented |
| PostgreSQL connection and JPA persistence | Implemented |
| Flyway database migration | Implemented |
| User registration | Implemented |
| Login and BCrypt credential verification | Implemented |
| JWT access-token generation | Implemented |
| RS256 signing and JWKS publication | Implemented |
| Automated test suite | 106 passing, 0 failures, 0 errors, 0 skipped |

## Architecture

The code uses dependency inversion to keep the core independent of delivery and infrastructure details:

```text
Interfaces / REST  ──┐
Infrastructure     ──┴──> Application ──> Domain
```

- **Domain** contains the identity model, value objects, invariants, repository contract, and domain exceptions. It has no Spring, HTTP, or persistence concerns.
- **Application** coordinates use cases and defines ports required from external systems. It depends on domain abstractions rather than concrete technologies.
- **Infrastructure** implements application and domain ports with JPA, PostgreSQL, BCrypt, UUID, Nimbus JOSE + JWT, and Spring configuration.
- **Interfaces / REST** translates HTTP requests into application commands and application results into HTTP responses.

This structure combines DDD modeling with Clean Architecture and Ports and Adapters. The modular monolith keeps deployment simple while preserving boundaries that can support later evolution.

## Maven modules

| Module | Responsibility |
|---|---|
| `shared-kernel` | Small foundation for stable concepts intentionally shared across modules. |
| `iam-application` | Main executable Spring Boot application; contains the current identity domain, use cases, adapters, REST API, migrations, and tests. |
| `api-gateway` | Module reserved for platform edge and gateway responsibilities. |

The root project uses Maven `pom` packaging. The executable application is currently `iam-application`.

## Identity domain

The implemented identity model includes:

- `User`, modeled as a rich domain entity without public setters
- `UserId`, `Email`, and `PasswordHash` value objects
- `UserStatus`
- the `UserRepository` domain contract
- domain exceptions for invalid identity data and duplicate users

State changes and validation are expressed through domain behavior rather than an anemic model.

## Application use cases and ports

The application layer currently provides:

- `RegisterUserUseCase`, with `RegisterUserCommand` and `RegisterUserResult`
- `LoginUserUseCase`, with `LoginUserCommand` and `LoginUserResult`

Its main outbound abstractions are:

- `UserRepository` for identity persistence
- `PasswordHasher` for hashing and verifying passwords
- `UserIdGenerator` for generating identity identifiers
- `AccessTokenIssuer` for issuing access tokens

User ID generation remains behind `UserIdGenerator`; password hashing and JWT creation are likewise delegated through ports rather than implemented inside the use cases.

## Infrastructure adapters

The current infrastructure includes:

- `JpaUserRepositoryAdapter`
- `SpringDataUserRepository`
- `UserJpaEntity`
- `UserPersistenceMapper`
- `BCryptPasswordHasher`
- `UuidUserIdGenerator`
- `Rs256JwtAccessTokenIssuer`
- `Rs256JwkProvider`

The application and domain layers do not depend directly on JPA, BCrypt, Nimbus, PostgreSQL, or HTTP. Concrete adapters and Spring bean wiring remain at the system boundary.

## Security

Passwords are hashed with BCrypt before persistence and verified through the `PasswordHasher` port during login.

Successful authentication produces a JWT access token signed with RS256:

- the RSA private key signs tokens and must remain secret
- the RSA public key is published through the JWKS endpoint
- the JWT header includes a `kid` that identifies the public key
- the issuer is `iam-platform`
- access tokens expire 15 minutes after issuance

Current JWT claims are:

| Claim | Meaning |
|---|---|
| `sub` | User ID |
| `iss` | Token issuer |
| `iat` | Issued-at time |
| `exp` | Expiration time |
| `email` | Authenticated user's email |
| `status` | Current user status |
| `token_type` | Token purpose (`access`) |

Refresh tokens and token rotation are not implemented yet.

## REST API

The examples below assume the application is available at `http://localhost:8080`.

### Register a user

`POST /identity/users/register` creates a new identity and returns `201 Created`.

```json
{
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "password": "StrongPass123"
}
```

Example successful response:

```json
{
  "id": "c84d9f2d-5349-4f4b-b9a3-0cbb2c75be89",
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-07-11T18:00:00Z"
}
```

Relevant status codes: `201 Created`, `400 Bad Request`, `409 Conflict`, and `415 Unsupported Media Type`.

### Login

`POST /auth/login` verifies credentials and returns `200 OK` with an access token.

```json
{
  "email": "alex@example.com",
  "password": "StrongPass123"
}
```

Example successful response:

```json
{
  "userId": "c84d9f2d-5349-4f4b-b9a3-0cbb2c75be89",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "accessToken": "eyJhbGciOiJSUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresAt": "2026-07-11T18:15:00Z"
}
```

Relevant status codes: `200 OK`, `400 Bad Request`, `401 Unauthorized`, and `415 Unsupported Media Type`.

### Read the JSON Web Key Set

`GET /.well-known/jwks.json` returns `200 OK` and exposes the public RSA key used to verify JWT signatures.

Example successful response:

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "local-dev-key",
      "use": "sig",
      "alg": "RS256",
      "n": "shortened-modulus...",
      "e": "AQAB"
    }
  ]
}
```

API errors use a consistent body containing `status`, `error`, `message`, `path`, and `timestamp`.

## Database

Local infrastructure uses PostgreSQL 16 through Docker Compose. Flyway owns schema migrations, Spring Data JPA provides persistence, and Hibernate runs with `ddl-auto: validate` to verify that entity mappings match the migrated schema.

The current `users` table stores the UUID identifier, name, unique email, BCrypt password hash, status, and creation/update timestamps. Connection values are configurable through environment variables.

## Tests

The automated test strategy covers:

- domain unit tests
- application use-case unit tests
- Mockito-based collaboration tests
- controller tests with MockMvc
- JPA adapter and persistence-mapper tests
- JWT signing and claims tests
- JWKS tests
- BCrypt tests
- Spring application-context loading
- PostgreSQL integration with Testcontainers
- Flyway migration validation

Current result: **106 tests, 0 failures, 0 errors, 0 skipped**.

## Local requirements

- Java 21
- Maven 3.9 or a compatible Maven 3 release
- Docker Desktop (or another Docker-compatible runtime)
- PostgreSQL 16 through the provided Docker Compose file
- OpenSSL for local RSA key generation

## Local configuration

The application reads these environment variables:

| Variable | Purpose | Local default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/iam_platform` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `iam_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `iam_password` |
| `JWT_PRIVATE_KEY` | PKCS#8 PEM private key used to sign tokens | Required |
| `JWT_PUBLIC_KEY` | X.509 PEM public key exposed through JWKS | Required |
| `JWT_KEY_ID` | Key identifier added as JWT/JWK `kid` | `local-dev-key` |

For Bash-compatible shells, load locally generated keys before starting the application:

```bash
export JWT_PRIVATE_KEY="$(cat private_key.pem)"
export JWT_PUBLIC_KEY="$(cat public_key.pem)"
export JWT_KEY_ID="local-dev-key"
```

For PowerShell:

```powershell
$env:JWT_PRIVATE_KEY = Get-Content ./private_key.pem -Raw
$env:JWT_PUBLIC_KEY = Get-Content ./public_key.pem -Raw
$env:JWT_KEY_ID = "local-dev-key"
```

Never commit private keys or `.env` files containing secrets. Keys generated for local development must not be reused in production.

## Generate local RSA keys

Generate a 2048-bit PKCS#8 RSA private key and derive its X.509 public key:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private_key.pem
openssl pkey -in private_key.pem -pubout -out public_key.pem
```

The resulting PEM files must use these headers:

```text
-----BEGIN PRIVATE KEY-----
-----BEGIN PUBLIC KEY-----
```

Store both files outside version control and expose their complete PEM contents through the environment variables above.

## Run locally

Start PostgreSQL from the project root:

```bash
docker compose up -d postgres
```

After configuring the RSA key variables, start the executable module:

```bash
mvn -pl iam-application spring-boot:run
```

Do not run `spring-boot:run` directly against the root project: the parent has `pom` packaging and is not the executable Spring Boot application.

## Run tests

From the project root:

```bash
mvn -pl iam-application -am clean test
```

Docker must be running because the integration suite uses Testcontainers with PostgreSQL.

## Manual validation flow

1. Call `GET /.well-known/jwks.json` and retain the returned `kid` and public key.
2. Register a user with `POST /identity/users/register`.
3. Log in with `POST /auth/login` using the registered credentials.
4. Confirm that the response contains a Bearer JWT access token.
5. Decode the JWT header and claims without placing the token in source control or logs.
6. Confirm that the header `kid` matches a key in JWKS and inspect the expected claims and expiration.
7. Repeat login with invalid credentials and confirm the API returns `401 Unauthorized`.

## Roadmap

The following capabilities are planned and are **not implemented yet**:

- refresh tokens and token rotation
- JWT validation and resource-server support
- role-based access control (RBAC) and permissions
- session management
- API keys
- audit logs
- multi-factor authentication (MFA)
- OAuth 2.0 and OpenID Connect
- signing-key rotation
- rate limiting
- observability
- selective evolution from the modular monolith into microservices where justified

## Development workflow

Development uses feature branches, small focused commits, manual review, and automated tests before merge. Commit messages follow Conventional Commits so history remains readable and changes are easy to classify.
