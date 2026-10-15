package com.mitocode.application.dto;

import com.mitocode.domain.model.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        @Schema(description = "Identificador de la reserva", example = "00000000-0000-4000-8000-000000000401") UUID id,
        @Schema(description = "Identificador de la función", example = "00000000-0000-4000-8000-000000000201") UUID screeningId,
        @Schema(description = "Título de la película al crear la reserva", example = "Horizonte final") String movieTitle,
        @Schema(description = "Identificador del cliente", example = "00000000-0000-4000-8000-000000000301") UUID customerId,
        @Schema(description = "Estado de la reserva", example = "CONFIRMED") BookingStatus status,
        @Schema(description = "Importe total de la reserva", example = "25.50") BigDecimal totalAmount,
        @Schema(description = "Fecha de creación en UTC", example = "2030-01-01T12:00:00Z") Instant createdAt,
        @Schema(description = "Fecha de última modificación en UTC", example = "2030-01-01T12:00:00Z") Instant updatedAt
) {
}
