package com.mitocode.ecommerce.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "username es obligatorio")
        String username,

        @NotBlank(message = "password es obligatorio")
        String password
) {
}
