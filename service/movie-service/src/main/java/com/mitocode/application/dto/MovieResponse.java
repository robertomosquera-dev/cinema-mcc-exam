package com.mitocode.application.dto;

import com.mitocode.domain.model.Genre;

import java.time.Instant;
import java.util.UUID;

public record MovieResponse(
        UUID id,
        String title,
        String director,
        Genre genre,
        Integer durationMin,
        String rating,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
