package com.mitocode.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBookingRequest(
        @Schema(description = "Identificador de la función", example = "00000000-0000-4000-8000-000000000201")
        @NotNull(message = "La función es obligatoria") UUID screeningId,
        @Schema(description = "Identificador del cliente", example = "00000000-0000-4000-8000-000000000301")
        @NotNull(message = "El cliente es obligatorio") UUID customerId,
        @Schema(description = "Importe total de la reserva", example = "25.50")
        @NotNull(message = "El importe total es obligatorio")
        @Positive(message = "El importe total debe ser mayor que cero") BigDecimal totalAmount
) {
}
