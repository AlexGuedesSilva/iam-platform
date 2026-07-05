package com.alexguedes.iam.identity.application.port.identity;

import com.alexguedes.iam.identity.domain.valueobject.UserId;

public interface UserIdGenerator {

    UserId generate();
}
