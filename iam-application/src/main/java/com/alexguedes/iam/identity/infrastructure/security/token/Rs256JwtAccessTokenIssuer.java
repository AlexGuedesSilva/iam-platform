package com.alexguedes.iam.identity.infrastructure.security.token;

import com.alexguedes.iam.identity.application.port.security.AccessTokenClaims;
import com.alexguedes.iam.identity.application.port.security.AccessTokenIssuer;
import com.alexguedes.iam.identity.application.port.security.IssuedAccessToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

public class Rs256JwtAccessTokenIssuer implements AccessTokenIssuer {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final RSAPrivateKey privateKey;
    private final String keyId;

    public Rs256JwtAccessTokenIssuer(RSAPrivateKey privateKey) {
        this(privateKey, null);
    }

    public Rs256JwtAccessTokenIssuer(RSAPrivateKey privateKey, String keyId) {
        if (privateKey == null) {
            throw new IllegalArgumentException("RSA private key must not be null");
        }
        this.privateKey = privateKey;
        this.keyId = keyId;
    }

    @Override
    public IssuedAccessToken issue(AccessTokenClaims claims) {
        if (claims == null) {
            throw new IllegalArgumentException("Access token claims must not be null");
        }

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .subject(claims.subject().value().toString())
                .issuer(claims.issuer())
                .issueTime(Date.from(claims.issuedAt()))
                .expirationTime(Date.from(claims.expiresAt()))
                .claim("email", claims.email().value())
                .claim("status", claims.status().name())
                .claim("token_type", claims.tokenType())
                .build();

        SignedJWT signedJwt = new SignedJWT(header(), jwtClaims);
        try {
            signedJwt.sign(new RSASSASigner(privateKey));
        } catch (JOSEException exception) {
            throw new IllegalStateException("Could not sign access token", exception);
        }

        return new IssuedAccessToken(signedJwt.serialize(), BEARER_TOKEN_TYPE, claims.expiresAt());
    }

    private JWSHeader header() {
        JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT);

        if (keyId != null && !keyId.isBlank()) {
            builder.keyID(keyId);
        }

        return builder.build();
    }
}
