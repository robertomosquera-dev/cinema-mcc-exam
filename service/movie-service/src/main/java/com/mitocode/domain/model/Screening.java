package com.mitocode.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Screening {

    private UUID id;

    private UUID movieId;

    private String room;

    private LocalDateTime showAt;

    private Integer totalSeats;

    private Integer availableSeats;

    private boolean active;
}
