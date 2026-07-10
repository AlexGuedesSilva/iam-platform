package com.alexguedes.iam.identity.interfaces.rest.jwks;

import com.alexguedes.iam.identity.infrastructure.security.token.Rs256JwkProvider;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {

    private final Rs256JwkProvider jwkProvider;

    public JwksController(Rs256JwkProvider jwkProvider) {
        this.jwkProvider = jwkProvider;
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> jwks() {
        return ResponseEntity.ok(jwkProvider.jwkSet());
    }
}
