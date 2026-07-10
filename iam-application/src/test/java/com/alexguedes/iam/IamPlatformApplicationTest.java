package com.alexguedes.iam;

import static org.mockito.Mockito.mock;

import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.infrastructure.security.token.Rs256JwkProvider;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest(
        classes = {
                IamPlatformApplication.class,
                IamPlatformApplicationTest.ContextTestConfiguration.class
        },
        properties = {
                "spring.datasource.url=jdbc:h2:mem:iam-platform-context-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=validate"
        }
)
class IamPlatformApplicationTest {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class ContextTestConfiguration {

        @Bean
        AccessTokenIssuer accessTokenIssuer() {
            return mock(AccessTokenIssuer.class);
        }

        @Bean
        Rs256JwkProvider rs256JwkProvider() {
            return mock(Rs256JwkProvider.class);
        }

        @Bean
        UserIdGenerator userIdGenerator() {
            return () -> new UserId(UUID.randomUUID());
        }
    }
}