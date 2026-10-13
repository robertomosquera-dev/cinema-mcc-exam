package com.mitocode.application.dto;

import java.util.UUID;

public record ScreeningAvailabilityResponse(
        UUID id,
        String movieTitle,
        Integer availableSeats,
        boolean active
) {
}
