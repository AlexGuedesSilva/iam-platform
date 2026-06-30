package com.alexguedes.iam.identity.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.domain.Email;
import com.alexguedes.iam.identity.domain.PasswordHash;
import com.alexguedes.iam.identity.domain.User;
import com.alexguedes.iam.identity.domain.UserId;
import com.alexguedes.iam.identity.domain.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = JpaUserRepositoryAdapterTest.PersistenceTestApplication.class)
class JpaUserRepositoryAdapterTest {

    private static final UUID USER_ID = UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd");
    private static final String NAME = "Alex Guedes";
    private static final String EMAIL = "alex@example.com";
    private static final String PASSWORD_HASH = "12345678901234567890123456789012";
    private static final Instant CREATED_AT = Instant.parse("2026-06-27T12:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-06-27T12:30:00Z");

    @Autowired
    private JpaUserRepositoryAdapter repositoryAdapter;

    @Test
    void shouldSaveUser() {
        User user = validUser();

        User savedUser = repositoryAdapter.save(user);

        assertEquals(user.id(), savedUser.id());
        assertEquals(user.name(), savedUser.name());
        assertEquals(user.email(), savedUser.email());
        assertEquals(user.passwordHash(), savedUser.passwordHash());
        assertEquals(user.status(), savedUser.status());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = validUser();
        repositoryAdapter.save(user);

        Optional<User> foundUser = repositoryAdapter.findByEmail(new Email(EMAIL));

        assertTrue(foundUser.isPresent());
        assertEquals(user.id(), foundUser.get().id());
        assertEquals(user.name(), foundUser.get().name());
        assertEquals(user.email(), foundUser.get().email());
        assertEquals(user.passwordHash(), foundUser.get().passwordHash());
        assertEquals(user.status(), foundUser.get().status());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> foundUser = repositoryAdapter.findByEmail(new Email("missing@example.com"));

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldFindUserById() {
        User user = validUser();
        repositoryAdapter.save(user);

        Optional<User> foundUser = repositoryAdapter.findById(user.id());

        assertTrue(foundUser.isPresent());
        assertEquals(user.id(), foundUser.get().id());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<User> foundUser = repositoryAdapter.findById(new UserId(UUID.randomUUID()));

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldCheckIfUserExistsByEmail() {
        repositoryAdapter.save(validUser());

        assertTrue(repositoryAdapter.existsByEmail(new Email(EMAIL)));
        assertFalse(repositoryAdapter.existsByEmail(new Email("missing@example.com")));
    }

    private static User validUser() {
        return new User(
                new UserId(USER_ID),
                NAME,
                new Email(EMAIL),
                new PasswordHash(PASSWORD_HASH),
                UserStatus.ACTIVE,
                CREATED_AT,
                UPDATED_AT
        );
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = UserJpaEntity.class)
    @EnableJpaRepositories(basePackageClasses = SpringDataUserRepository.class)
    @Import({JpaUserRepositoryAdapter.class, UserPersistenceMapper.class})
    static class PersistenceTestApplication {
    }
}
