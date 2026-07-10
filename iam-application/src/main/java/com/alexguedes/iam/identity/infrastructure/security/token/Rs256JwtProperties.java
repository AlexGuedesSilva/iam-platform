package com.alexguedes.iam.identity.infrastructure.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iam.security.jwt.rs256")
public record Rs256JwtProperties(String privateKey, String keyId) {
}
