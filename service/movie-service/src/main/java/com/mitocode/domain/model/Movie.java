package com.mitocode.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import com.mitocode.domain.model.exception.InvalidMovieStateException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Movie {

    private UUID id;

    private String title;

    private String director;

    private Genre genre;

    private Integer durationMin;

    private String rating;

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;

    private Instant updatedAt;

    public Movie(UUID id) {
        this.id = id;
    }

    @Builder.Default
    private List<Screening> screenings = new ArrayList<>();

    public void updateDetails(String title, String director, Genre genre, Integer durationMin, String rating) {
        if (title == null || title.isBlank()
                || director == null || director.isBlank()
                || genre == null
                || durationMin == null || durationMin <= 0
                || rating == null || rating.isBlank()) {
            throw new IllegalArgumentException("Los datos de la película son obligatorios y la duración debe ser positiva");
        }

        this.title = title;
        this.director = director;
        this.genre = genre;
        this.durationMin = durationMin;
        this.rating = rating;
    }

    public void activate() {
        if (active) {
            throw new InvalidMovieStateException("La película ya está activa.");
        }
        this.active = true;
    }

    public void deactivate() {
        if (!active) {
            throw new InvalidMovieStateException("La película ya está inactiva.");
        }
        this.active = false;
    }


}
