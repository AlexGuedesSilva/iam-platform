package com.alexguedes.iam.identity.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.infrastructure.persistence.user.SpringDataUserRepository;
import com.alexguedes.iam.identity.infrastructure.persistence.user.UserJpaEntity;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = IdentityDatabaseMigrationTest.PersistenceTestApplication.class)
class IdentityDatabaseMigrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SpringDataUserRepository repository;

    @Test
    void shouldCreateUsersTableWithExpectedColumnsAndConstraints() {
        assertTrue(usersTableExists());

        Map<String, ColumnDefinition> columns = usersColumns();

        assertColumn(columns, "id", "uuid", null, false);
        assertColumn(columns, "name", "character varying", 100, false);
        assertColumn(columns, "email", "character varying", 255, false);
        assertColumn(columns, "password_hash", "character varying", 255, false);
        assertColumn(columns, "status", "character varying", 20, false);
        assertColumn(columns, "created_at", "timestamp with time zone", null, false);
        assertColumn(columns, "updated_at", "timestamp with time zone", null, false);

        assertTrue(hasPrimaryKeyOnColumn("users", "id"));
        assertTrue(hasUniqueConstraintOnColumn("users", "email"));
    }

    @Test
    void shouldPersistUserJpaEntityUsingFlywayCreatedTable() {
        UUID userId = UUID.fromString("57328fa1-f2e7-4eeb-b730-adb973bf6cb0");
        Instant now = Instant.parse("2026-07-03T12:00:00Z");

        UserJpaEntity entity = new UserJpaEntity(
                userId,
                "Alex Guedes",
                "alex.migration@example.com",
                "12345678901234567890123456789012",
                "ACTIVE",
                now,
                now
        );

        assertDoesNotThrow(() -> repository.saveAndFlush(entity));

        UserJpaEntity savedUser = repository.findById(userId).orElseThrow();

        assertEquals(entity.getId(), savedUser.getId());
        assertEquals(entity.getEmail(), savedUser.getEmail());
        assertEquals(entity.getStatus(), savedUser.getStatus());
    }

    private boolean usersTableExists() {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                      AND table_name = 'users'
                )
                """, Boolean.class);

        return Boolean.TRUE.equals(exists);
    }

    private Map<String, ColumnDefinition> usersColumns() {
        return jdbcTemplate.query("""
                        SELECT column_name, data_type, character_maximum_length, is_nullable
                        FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = 'users'
                        """,
                (resultSet, rowNumber) -> new ColumnDefinition(
                        resultSet.getString("column_name"),
                        resultSet.getString("data_type"),
                        (Integer) resultSet.getObject("character_maximum_length"),
                        "YES".equals(resultSet.getString("is_nullable"))
                ))
                .stream()
                .collect(Collectors.toMap(ColumnDefinition::name, column -> column));
    }

    private boolean hasPrimaryKeyOnColumn(String tableName, String columnName) {
        return hasSingleColumnConstraint(tableName, columnName, "p");
    }

    private boolean hasUniqueConstraintOnColumn(String tableName, String columnName) {
        return hasSingleColumnConstraint(tableName, columnName, "u");
    }

    private boolean hasSingleColumnConstraint(String tableName, String columnName, String constraintType) {
        Boolean exists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM pg_constraint constraint_definition
                    JOIN pg_class table_definition
                      ON table_definition.oid = constraint_definition.conrelid
                    JOIN pg_namespace namespace_definition
                      ON namespace_definition.oid = table_definition.relnamespace
                    WHERE namespace_definition.nspname = 'public'
                      AND table_definition.relname = ?
                      AND constraint_definition.contype = ?
                      AND constraint_definition.conkey = ARRAY[
                          (
                              SELECT attribute.attnum
                              FROM pg_attribute attribute
                              WHERE attribute.attrelid = table_definition.oid
                                AND attribute.attname = ?
                                AND NOT attribute.attisdropped
                          )
                      ]::smallint[]
                )
                """, Boolean.class, tableName, constraintType, columnName);

        return Boolean.TRUE.equals(exists);
    }

    private void assertColumn(
            Map<String, ColumnDefinition> columns,
            String name,
            String dataType,
            Integer maxLength,
            boolean nullable
    ) {
        ColumnDefinition column = columns.get(name);

        assertNotNull(column);
        assertEquals(dataType, column.dataType());
        assertEquals(maxLength, column.maxLength());
        assertEquals(nullable, column.nullable());
    }

    private record ColumnDefinition(String name, String dataType, Integer maxLength, boolean nullable) {
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = UserJpaEntity.class)
    @EnableJpaRepositories(basePackageClasses = SpringDataUserRepository.class)
    static class PersistenceTestApplication {
    }
}
