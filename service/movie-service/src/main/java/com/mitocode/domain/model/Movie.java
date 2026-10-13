package com.mitocode.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
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
}
