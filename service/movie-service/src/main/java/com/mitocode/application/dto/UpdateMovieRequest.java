package com.mitocode.application.dto;

import com.mitocode.domain.model.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateMovieRequest(
        @NotBlank(message = "El título es obligatorio") String title,
        @NotBlank(message = "El director es obligatorio") String director,
        @NotNull(message = "El género es obligatorio") Genre genre,
        @NotNull(message = "La duración es obligatoria")
        @Positive(message = "La duración debe ser mayor que cero") Integer durationMin,
        @NotBlank(message = "La clasificación es obligatoria") String rating
) {
}
