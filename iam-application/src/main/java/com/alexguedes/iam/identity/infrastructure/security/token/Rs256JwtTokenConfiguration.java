package com.alexguedes.iam.identity.infrastructure.security.token;

import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Rs256JwtProperties.class)
public class Rs256JwtTokenConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "iam.security.jwt.rs256", name = "private-key")
    AccessTokenIssuer accessTokenIssuer(Rs256JwtProperties properties) {
        return new Rs256JwtAccessTokenIssuer(parsePrivateKey(properties.privateKey()), properties.keyId());
    }

    @Bean
    @ConditionalOnProperty(prefix = "iam.security.jwt.rs256", name = "public-key")
    Rs256JwkProvider rs256JwkProvider(Rs256JwtProperties properties) {
        return new Rs256JwkProvider(parsePublicKey(properties.publicKey()), properties.keyId());
    }

    private RSAPrivateKey parsePrivateKey(String privateKeyPem) {
        if (privateKeyPem == null || privateKeyPem.isBlank()) {
            throw new IllegalArgumentException("RS256 private key must not be blank");
        }

        String privateKeyContent = privateKeyPem
                .replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalArgumentException("RS256 private key must be a valid PKCS#8 PEM RSA private key", exception);
        }
    }

    private RSAPublicKey parsePublicKey(String publicKeyPem) {
        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            throw new IllegalArgumentException("RS256 public key must not be blank");
        }

        String publicKeyContent = publicKeyPem
                .replace("\\n", "\n")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalArgumentException("RS256 public key must be a valid X.509 PEM RSA public key", exception);
        }
    }
}
