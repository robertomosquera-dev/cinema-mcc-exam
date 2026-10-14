package com.mitocode.domain.model;

import com.mitocode.domain.model.exception.InvalidScreeningCapacityException;
import com.mitocode.domain.model.exception.NoActiveScreeningException;
import com.mitocode.domain.model.exception.NoSeatsAvailableException;
import com.mitocode.domain.model.exception.SeatsAlreadyFullException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Screening {

    private UUID id;

    private Movie movie;

    private String room;

    private LocalDateTime showAt;

    private Integer totalSeats;

    private Integer availableSeats;

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;

    private Instant updatedAt;

    public void validateCapacity() {
        if (totalSeats == null || totalSeats <= 0
                || availableSeats == null || availableSeats < 0
                || availableSeats > totalSeats) {
            throw new InvalidScreeningCapacityException();
        }
    }

    public void reserveSeat() {
        if(!active){
            throw new NoActiveScreeningException();
        }
        if(availableSeats <= 0){
            throw new NoSeatsAvailableException();
        }
        availableSeats--;
    }

    public void releaseSeat() {
        if(!active){
            throw new NoActiveScreeningException();
        }
        if(availableSeats>=totalSeats){
            throw new SeatsAlreadyFullException();
        }
        availableSeats++;
    }

}
