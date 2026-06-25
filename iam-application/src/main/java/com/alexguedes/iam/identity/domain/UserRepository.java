package com.alexguedes.iam.identity.domain;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
