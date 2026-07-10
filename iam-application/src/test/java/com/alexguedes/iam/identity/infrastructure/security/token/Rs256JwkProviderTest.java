package com.alexguedes.iam.identity.infrastructure.security.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Rs256JwkProviderTest {

    @Test
    void shouldPublishOnlyPublicRs256Jwk() throws Exception {
        KeyPair keyPair = generateKeyPair();
        Rs256JwkProvider provider = new Rs256JwkProvider(
                (RSAPublicKey) keyPair.getPublic(),
                "local-dev-key"
        );

        Map<String, Object> jwkSet = provider.jwkSet();

        List<?> keys = (List<?>) jwkSet.get("keys");
        assertEquals(1, keys.size());

        Map<?, ?> jwk = (Map<?, ?>) keys.getFirst();
        assertEquals("RSA", jwk.get("kty"));
        assertEquals("sig", jwk.get("use"));
        assertEquals("RS256", jwk.get("alg"));
        assertEquals("local-dev-key", jwk.get("kid"));
        assertNotNull(jwk.get("n"));
        assertNotNull(jwk.get("e"));
        assertFalse(jwk.containsKey("d"));
        assertFalse(jwk.containsKey("p"));
        assertFalse(jwk.containsKey("q"));
        assertFalse(jwk.containsKey("dp"));
        assertFalse(jwk.containsKey("dq"));
        assertFalse(jwk.containsKey("qi"));
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
