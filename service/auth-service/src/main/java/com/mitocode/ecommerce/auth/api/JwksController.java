package com.mitocode.ecommerce.auth.api;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class JwksController {

    private final JWKSet jwkSet;

    public JwksController(JWKSet jwkSet) {
        this.jwkSet = Objects.requireNonNull(jwkSet, "jwkSet must not be null");
    }

    @GetMapping(value = "/oauth2/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jwks() {
        return jwkSet.toPublicJWKSet().toJSONObject();
    }
}
