package com.alexguedes.iam.identity.infrastructure.persistence.user;

import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.model.User;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.alexguedes.iam.identity.domain.port.UserRepository;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository, UserPersistenceMapper userPersistenceMapper) {
        this.springDataUserRepository = springDataUserRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity savedEntity = springDataUserRepository.save(userPersistenceMapper.toJpaEntity(user));
        return userPersistenceMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springDataUserRepository.findById(id.value())
                .map(userPersistenceMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataUserRepository.findByEmail(email.value())
                .map(userPersistenceMapper::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataUserRepository.existsByEmail(email.value());
    }
}
