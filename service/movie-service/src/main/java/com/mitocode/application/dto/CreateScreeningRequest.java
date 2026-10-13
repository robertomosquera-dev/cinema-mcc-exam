package com.mitocode.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateScreeningRequest(
        @NotNull(message = "La película es obligatoria") UUID movieId,
        @NotBlank(message = "La sala es obligatoria") String room,
        @NotNull(message = "La fecha y hora de la función son obligatorias") LocalDateTime showAt,
        @NotNull(message = "La capacidad total es obligatoria")
        @Positive(message = "La capacidad total debe ser mayor que cero") Integer totalSeats,
        @NotNull(message = "Los asientos disponibles son obligatorios")
        @PositiveOrZero(message = "Los asientos disponibles no pueden ser negativos") Integer availableSeats
) {
}
