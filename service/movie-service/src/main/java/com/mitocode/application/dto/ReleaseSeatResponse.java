package com.mitocode.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReleaseSeatResponse(
        @NotNull UUID screeningId,
        @Positive Integer availableSeats
) {
}
