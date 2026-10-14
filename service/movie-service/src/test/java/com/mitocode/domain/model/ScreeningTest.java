package com.mitocode.domain.model;

import com.mitocode.domain.model.exception.InvalidScreeningCapacityException;
import com.mitocode.domain.model.exception.NoActiveScreeningException;
import com.mitocode.domain.model.exception.NoSeatsAvailableException;
import com.mitocode.domain.model.exception.SeatsAlreadyFullException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScreeningTest {

    @Test
    void capacityAllowsZeroAvailableSeatsButRejectsAnExcess() {
        assertDoesNotThrow(() -> screening(2, 0, true).validateCapacity());
        assertThrows(InvalidScreeningCapacityException.class,
                () -> screening(2, 3, true).validateCapacity());
    }

    @Test
    void reservationNeverMakesAvailabilityNegative() {
        Screening screening = screening(1, 1, true);

        screening.reserveSeat();

        assertEquals(0, screening.getAvailableSeats());
        assertThrows(NoSeatsAvailableException.class, screening::reserveSeat);
        assertEquals(0, screening.getAvailableSeats());
    }

    @Test
    void releaseNeverExceedsTotalCapacity() {
        Screening screening = screening(1, 0, true);

        screening.releaseSeat();

        assertEquals(1, screening.getAvailableSeats());
        assertThrows(SeatsAlreadyFullException.class, screening::releaseSeat);
        assertEquals(1, screening.getAvailableSeats());
    }

    @Test
    void inactiveScreeningRejectsSeatChanges() {
        Screening screening = screening(2, 1, false);

        assertThrows(NoActiveScreeningException.class, screening::reserveSeat);
        assertThrows(NoActiveScreeningException.class, screening::releaseSeat);
        assertEquals(1, screening.getAvailableSeats());
    }

    private Screening screening(int totalSeats, int availableSeats, boolean active) {
        return Screening.builder()
                .totalSeats(totalSeats)
                .availableSeats(availableSeats)
                .active(active)
                .build();
    }
}
