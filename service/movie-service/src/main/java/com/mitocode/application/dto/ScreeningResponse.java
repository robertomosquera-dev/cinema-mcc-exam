package com.mitocode.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScreeningResponse(
        UUID id,
        UUID movieId,
        String movieTitle,
        String room,
        LocalDateTime showAt,
        Integer totalSeats,
        Integer availableSeats,
        boolean active
) {
}
