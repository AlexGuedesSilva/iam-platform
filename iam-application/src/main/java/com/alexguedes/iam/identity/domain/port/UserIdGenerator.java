package com.alexguedes.iam.identity.domain.port;

import com.alexguedes.iam.identity.domain.valueobject.UserId;

public interface UserIdGenerator {

    UserId generate();
}
