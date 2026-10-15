package com.mitocode.application.service;

import com.mitocode.application.port.out.MovieRepositoryPort;
import com.mitocode.application.port.out.ScreeningRepositoryPort;
import com.mitocode.application.port.out.SeatReservationPort;
import com.mitocode.application.mapper.ScreeningDtoMapper;
import com.mitocode.domain.model.Screening;
import com.mitocode.domain.model.exception.NoSeatsAvailableException;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScreeningIdempotencyTest {
    @Test
    void retryDoesNotDiscountTwiceAndReleaseIsIdempotent() {
        UUID screeningId=UUID.randomUUID(), bookingId=UUID.randomUUID();
        var screening=Screening.builder().id(screeningId).active(true)
                .totalSeats(2).availableSeats(2).build();
        var screenings=mock(ScreeningRepositoryPort.class);
        var reservations=mock(SeatReservationPort.class);
        var stored=new AtomicReference<SeatReservationPort.SeatReservation>();
        when(screenings.findByIdForUpdate(screeningId)).thenReturn(Optional.of(screening));
        when(screenings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservations.findByBookingId(bookingId)).thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        doAnswer(invocation -> {
            stored.set(new SeatReservationPort.SeatReservation(invocation.getArgument(0),
                    invocation.getArgument(1), invocation.getArgument(2)));
            return null;
        }).when(reservations).save(any(),any(),anyBoolean());
        var service=new ScreeningService(mock(MovieRepositoryPort.class), screenings, reservations,
                mock(ScreeningDtoMapper.class));
        assertEquals(1, service.reserveSeat(screeningId, bookingId).availableSeats());
        assertEquals(1, service.reserveSeat(screeningId, bookingId).availableSeats());
        assertEquals(1, screening.getAvailableSeats());
        assertEquals(2, service.releaseSeat(screeningId, bookingId).availableSeats());
        assertEquals(2, service.releaseSeat(screeningId, bookingId).availableSeats());
        assertThrows(NoSeatsAvailableException.class, () -> service.reserveSeat(screeningId, bookingId));
    }

    @Test
    void releaseBeforeLateReserveCreatesTombstone() {
        UUID screeningId=UUID.randomUUID(), bookingId=UUID.randomUUID();
        var screening=Screening.builder().id(screeningId).active(true)
                .totalSeats(2).availableSeats(2).build();
        var screenings=mock(ScreeningRepositoryPort.class);
        var reservations=mock(SeatReservationPort.class);
        var stored=new AtomicReference<SeatReservationPort.SeatReservation>();
        when(screenings.findByIdForUpdate(screeningId)).thenReturn(Optional.of(screening));
        when(reservations.findByBookingId(bookingId)).thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        doAnswer(invocation -> {
            stored.set(new SeatReservationPort.SeatReservation(invocation.getArgument(0),
                    invocation.getArgument(1), invocation.getArgument(2)));
            return null;
        }).when(reservations).save(any(),any(),anyBoolean());
        var service=new ScreeningService(mock(MovieRepositoryPort.class), screenings, reservations,
                mock(ScreeningDtoMapper.class));
        assertEquals(2, service.releaseSeat(screeningId, bookingId).availableSeats());
        assertThrows(NoSeatsAvailableException.class, () -> service.reserveSeat(screeningId, bookingId));
        assertEquals(2, screening.getAvailableSeats());
    }
}
