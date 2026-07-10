package com.alexguedes.iam;

import com.alexguedes.iam.identity.application.port.identity.UserIdGenerator;
import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.infrastructure.security.token.Rs256JwkProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = IamPlatformApplication.class,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:iam-platform-context-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=validate"
        }
)
class IamPlatformApplicationTest {

    @MockitoBean
    private AccessTokenIssuer accessTokenIssuer;

    @MockitoBean
    private Rs256JwkProvider rs256JwkProvider;

    @MockitoBean
    private UserIdGenerator userIdGenerator;

    @Test
    void contextLoads() {
    }
}