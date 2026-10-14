package com.mitocode.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScreeningResponse(
        @Schema(description = "Identificador de la función", example = "00000000-0000-4000-8000-000000000201") UUID id,
        @Schema(description = "Identificador de la película", example = "00000000-0000-4000-8000-000000000101") UUID movieId,
        @Schema(description = "Título de la película", example = "Horizonte final") String movieTitle,
        @Schema(description = "Sala de la función", example = "Sala 1") String room,
        @Schema(description = "Fecha y hora local de la función", example = "2030-01-01T18:00:00") LocalDateTime showAt,
        @Schema(description = "Capacidad total", example = "80") Integer totalSeats,
        @Schema(description = "Asientos disponibles", example = "79") Integer availableSeats,
        @Schema(description = "Indica si la función está activa", example = "true") boolean active
) {
}
