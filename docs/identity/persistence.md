# Identity persistence

The domain model and database model are deliberately separate.

| Component | Responsibility |
|---|---|
| `User` | Domain entity and business invariants. |
| `UserPersistenceMapper` | Converts between domain and persistence representations. |
| `UserJpaEntity` | JPA-specific table mapping. |
| `SpringDataUserRepository` | Spring Data persistence operations. |
| `JpaUserRepositoryAdapter` | Implements the core `UserRepository` contract. |

The adapter keeps JPA annotations, repository APIs, and database behavior out of the domain.

## Database schema

Flyway creates the PostgreSQL `users` table with UUID primary key, name, unique email, password hash, status, and timestamps. Hibernate uses `ddl-auto: validate`; it checks mappings but does not own schema creation.

PostgreSQL 16 is used locally and in Testcontainers migration tests. See the [persistence mapping diagram](../diagrams/README.md#persistence-mapping).
