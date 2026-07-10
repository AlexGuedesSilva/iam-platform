package com.alexguedes.iam.identity.infrastructure.security.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexguedes.iam.identity.application.port.security.AccessTokenClaims;
import com.alexguedes.iam.identity.application.port.security.IssuedAccessToken;
import com.alexguedes.iam.identity.domain.model.UserStatus;
import com.alexguedes.iam.identity.domain.valueobject.Email;
import com.alexguedes.iam.identity.domain.valueobject.UserId;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class Rs256JwtAccessTokenIssuerTest {

    @Test
    void shouldIssueRs256SignedAccessToken() throws Exception {
        KeyPair keyPair = generateKeyPair();
        Rs256JwtAccessTokenIssuer issuer = new Rs256JwtAccessTokenIssuer(
                (RSAPrivateKey) keyPair.getPrivate(),
                "local-dev-key"
        );
        Instant issuedAt = Instant.parse("2026-07-09T12:00:00Z");
        Instant expiresAt = Instant.parse("2026-07-09T12:15:00Z");
        UserId userId = new UserId(UUID.fromString("85d2a3fe-a82f-45de-ac1a-05d71147e8fd"));
        AccessTokenClaims claims = new AccessTokenClaims(
                userId,
                new Email("alex@example.com"),
                UserStatus.ACTIVE,
                "iam-platform",
                issuedAt,
                expiresAt,
                "access"
        );

        IssuedAccessToken token = issuer.issue(claims);

        SignedJWT signedJwt = SignedJWT.parse(token.value());
        JWTClaimsSet jwtClaims = signedJwt.getJWTClaimsSet();
        assertTrue(signedJwt.verify(new RSASSAVerifier((RSAPublicKey) keyPair.getPublic())));
        assertEquals(JWSAlgorithm.RS256, signedJwt.getHeader().getAlgorithm());
        assertEquals("local-dev-key", signedJwt.getHeader().getKeyID());
        assertEquals(userId.value().toString(), jwtClaims.getSubject());
        assertEquals("iam-platform", jwtClaims.getIssuer());
        assertEquals(issuedAt, jwtClaims.getIssueTime().toInstant());
        assertEquals(expiresAt, jwtClaims.getExpirationTime().toInstant());
        assertEquals("alex@example.com", jwtClaims.getStringClaim("email"));
        assertEquals("ACTIVE", jwtClaims.getStringClaim("status"));
        assertEquals("access", jwtClaims.getStringClaim("token_type"));
        assertEquals("Bearer", token.tokenType());
        assertEquals(expiresAt, token.expiresAt());
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
