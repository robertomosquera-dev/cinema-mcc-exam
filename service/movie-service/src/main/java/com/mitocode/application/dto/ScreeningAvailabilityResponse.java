package com.mitocode.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record ScreeningAvailabilityResponse(
        @Schema(description = "Identificador de la función", example = "00000000-0000-4000-8000-000000000201") UUID id,
        @Schema(description = "Título de la película para la reserva", example = "Horizonte final") String movieTitle,
        @Schema(description = "Asientos disponibles", example = "79") Integer availableSeats,
        @Schema(description = "Indica si la función está activa", example = "true") boolean active
) {
}
