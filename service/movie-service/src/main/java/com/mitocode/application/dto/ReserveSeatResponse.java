package com.mitocode.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReserveSeatResponse(
        @NotNull UUID screeningId,
        @Positive Integer availableSeats
) {
}
