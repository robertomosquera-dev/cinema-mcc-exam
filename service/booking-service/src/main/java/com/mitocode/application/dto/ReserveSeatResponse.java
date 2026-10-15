package com.mitocode.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ReserveSeatResponse(
        @Schema(description = "Identificador de la función", example = "00000000-0000-4000-8000-000000000201")
        @NotNull UUID screeningId,
        @Schema(description = "Asientos disponibles después de reservar", example = "79")
        @PositiveOrZero Integer availableSeats
) {
}
