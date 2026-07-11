package com.alexguedes.iam.identity.infrastructure.identity;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.alexguedes.iam.identity.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

class UuidUserIdGeneratorTest {

    private final UuidUserIdGenerator userIdGenerator = new UuidUserIdGenerator();

    @Test
    void shouldGenerateNonNullUserId() {
        UserId userId = userIdGenerator.generate();

        assertNotNull(userId);
    }

    @Test
    void shouldGenerateDifferentUserIds() {
        UserId firstUserId = userIdGenerator.generate();
        UserId secondUserId = userIdGenerator.generate();

        assertNotEquals(firstUserId, secondUserId);
    }
}
