package com.mitocode.ecommerce.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType
) {
    public static TokenResponse bearer(String accessToken, long expiresIn) {
        return new TokenResponse(accessToken, expiresIn, "Bearer");
    }
}
