package com.mitocode.application.dto;

import com.mitocode.domain.model.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovieRequest(
        @Schema(description = "Título de la película", example = "Horizonte final")
        @NotBlank(message = "El título es obligatorio") String title,
        @Schema(description = "Director de la película", example = "Ana Torres")
        @NotBlank(message = "El director es obligatorio") String director,
        @Schema(description = "Género de la película", example = "SCI_FI")
        @NotNull(message = "El género es obligatorio") Genre genre,
        @Schema(description = "Duración en minutos", example = "124")
        @NotNull(message = "La duración es obligatoria")
        @Positive(message = "La duración debe ser mayor que cero") Integer durationMin,
        @Schema(description = "Clasificación por edades", example = "PG-13")
        @NotBlank(message = "La clasificación es obligatoria") String rating
) {
}
