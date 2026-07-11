package com.alexguedes.iam.identity.infrastructure.identity;

import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import java.util.UUID;

public final class UuidUserIdGenerator implements UserIdGenerator {

    @Override
    public UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}
