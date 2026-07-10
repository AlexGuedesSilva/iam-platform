package com.alexguedes.iam.identity.infrastructure.security.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public class Rs256JwkProvider {

    private final RSAPublicKey publicKey;
    private final String keyId;

    public Rs256JwkProvider(RSAPublicKey publicKey, String keyId) {
        if (publicKey == null) {
            throw new IllegalArgumentException("RSA public key must not be null");
        }
        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("RSA key ID must not be blank");
        }
        this.publicKey = publicKey;
        this.keyId = keyId;
    }

    public Map<String, Object> jwkSet() {
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}
