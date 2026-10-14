package com.mitocode.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateScreeningRequest(
        @Schema(description = "Identificador de la película", example = "00000000-0000-4000-8000-000000000101")
        @NotNull(message = "La película es obligatoria") UUID movieId,
        @Schema(description = "Sala de la función", example = "Sala 1")
        @NotBlank(message = "La sala es obligatoria") String room,
        @Schema(description = "Fecha y hora local de la función", example = "2030-01-01T18:00:00")
        @NotNull(message = "La fecha y hora de la función son obligatorias") LocalDateTime showAt,
        @Schema(description = "Capacidad total de la sala", example = "80")
        @NotNull(message = "La capacidad total es obligatoria")
        @Positive(message = "La capacidad total debe ser mayor que cero") Integer totalSeats,
        @Schema(description = "Asientos disponibles al crear la función", example = "80")
        @NotNull(message = "Los asientos disponibles son obligatorios")
        @PositiveOrZero(message = "Los asientos disponibles no pueden ser negativos") Integer availableSeats
) {
}
