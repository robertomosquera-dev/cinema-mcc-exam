package com.mitocode.application.dto;

import com.mitocode.domain.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record MovieResponse(
        @Schema(description = "Identificador de la película", example = "00000000-0000-4000-8000-000000000101") UUID id,
        @Schema(description = "Título de la película", example = "Horizonte final") String title,
        @Schema(description = "Director de la película", example = "Ana Torres") String director,
        @Schema(description = "Género de la película", example = "SCI_FI") Genre genre,
        @Schema(description = "Duración en minutos", example = "124") Integer durationMin,
        @Schema(description = "Clasificación por edades", example = "PG-13") String rating,
        @Schema(description = "Indica si la película está activa", example = "true") boolean active,
        @Schema(description = "Fecha de creación en UTC", example = "2030-01-01T12:00:00Z") Instant createdAt,
        @Schema(description = "Fecha de última modificación en UTC", example = "2030-01-01T12:00:00Z") Instant updatedAt
) {
}
