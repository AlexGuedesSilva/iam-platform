package com.alexguedes.iam.identity.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.PasswordHash;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import java.time.Instant;
import java.util.UUID;

import com.alexguedes.iam.identity.infrastructure.persistence.user.UserJpaEntity;
import com.alexguedes.iam.identity.infrastructure.persistence.user.UserPersistenceMapper;
import org.junit.jupiter.api.Test;

class UserPersistenceMapperTest {

    private static final UUID USER_ID = UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd");
    private static final String NAME = "Alex Guedes";
    private static final String EMAIL = "alex@example.com";
    private static final String PASSWORD_HASH = "12345678901234567890123456789012";
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2026-06-27T12:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-06-27T12:30:00Z");

    private final UserPersistenceMapper mapper = new UserPersistenceMapper();

    @Test
    void shouldMapUserDomainEntityToUserJpaEntity() {
        User user = new User(
                new UserId(USER_ID),
                NAME,
                new Email(EMAIL),
                new PasswordHash(PASSWORD_HASH),
                STATUS,
                CREATED_AT,
                UPDATED_AT
        );

        UserJpaEntity entity = mapper.toJpaEntity(user);

        assertEquals(USER_ID, entity.getId());
        assertEquals(NAME, entity.getName());
        assertEquals(EMAIL, entity.getEmail());
        assertEquals(PASSWORD_HASH, entity.getPasswordHash());
        assertEquals(STATUS.name(), entity.getStatus());
        assertEquals(CREATED_AT, entity.getCreatedAt());
        assertEquals(UPDATED_AT, entity.getUpdatedAt());
    }

    @Test
    void shouldMapUserJpaEntityToUserDomainEntity() {
        UserJpaEntity entity = new UserJpaEntity(
                USER_ID,
                NAME,
                EMAIL,
                PASSWORD_HASH,
                STATUS.name(),
                CREATED_AT,
                UPDATED_AT
        );

        User user = mapper.toDomainEntity(entity);

        assertEquals(new UserId(USER_ID), user.id());
        assertEquals(NAME, user.name());
        assertEquals(new Email(EMAIL), user.email());
        assertEquals(new PasswordHash(PASSWORD_HASH), user.passwordHash());
        assertEquals(STATUS, user.status());
        assertEquals(CREATED_AT, user.createdAt());
        assertEquals(UPDATED_AT, user.updatedAt());
    }

    @Test
    void shouldRejectUserStatusInvalid() {
        UserJpaEntity entity = new UserJpaEntity(
                USER_ID,
                NAME,
                EMAIL,
                PASSWORD_HASH,
                "nome",
                CREATED_AT,
                UPDATED_AT
        );

        assertThrows(IllegalStateException.class, () -> mapper.toDomainEntity(entity));


    }
}
