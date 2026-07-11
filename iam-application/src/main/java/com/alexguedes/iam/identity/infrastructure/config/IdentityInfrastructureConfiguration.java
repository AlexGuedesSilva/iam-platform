package com.alexguedes.iam.identity.infrastructure.config;

import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.infrastructure.identity.UuidUserIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityInfrastructureConfiguration {

    @Bean
    UserIdGenerator userIdGenerator() {
        return new UuidUserIdGenerator();
    }
}
