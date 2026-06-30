package com.alexguedes.iam.identity.infrastructure.persistence;

import com.alexguedes.iam.identity.domain.*;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.name(),
                user.email().value(),
                user.passwordHash().value(),
                user.status().name(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public User toDomainEntity(UserJpaEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getName(),
                new Email(entity.getEmail()),
                new PasswordHash(entity.getPasswordHash()),
                toUserStatus(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private UserStatus toUserStatus(String status) {
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid user status stored in database: " + status);
        }
    }
}
