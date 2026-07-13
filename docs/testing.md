# Testing strategy

IAM Platform uses a layered test strategy so core behavior remains fast to verify while infrastructure boundaries receive focused integration coverage.

| Area | Current coverage |
|---|---|
| Domain | Unit tests for `User`, `UserId`, `Email`, and `PasswordHash` invariants and behavior. |
| Use cases | Registration and login tests; Mockito isolates repository and security collaborators where appropriate. |
| REST | MockMvc controller tests for registration, login, JWKS, validation, and error mapping. |
| Persistence | Mapper and JPA repository adapter tests. |
| Migrations | Flyway validation against PostgreSQL 16 in Testcontainers. |
| Security | BCrypt, RS256 JWT claims/signature, and JWK publication tests. |
| Bootstrap | Spring application-context load test. |

Verified current result:

```text
Tests:    106
Failures: 0
Errors:   0
Skipped:  0
```

Run from the project root:

```bash
mvn -pl iam-application -am clean test
```

Docker must be running for PostgreSQL Testcontainers tests. See [local development](local-development.md).
