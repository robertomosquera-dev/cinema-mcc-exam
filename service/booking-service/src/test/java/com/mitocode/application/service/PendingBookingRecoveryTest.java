package com.mitocode.application.service;

import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.domain.model.Booking;
import com.mitocode.domain.model.BookingStatus;
import com.mitocode.infrastructure.scheduling.PendingBookingRecoveryJob;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PendingBookingRecoveryTest {
    private final BookingRepositoryPort repository = mock(BookingRepositoryPort.class);
    private final MovieClientPort movie = mock(MovieClientPort.class);
    private final PendingBookingRecoveryService recovery = new PendingBookingRecoveryService(repository, movie);

    private Booking pending() {
        return Booking.builder().id(UUID.randomUUID()).screeningId(UUID.randomUUID())
                .movieTitle("Título snapshot").customerId(UUID.randomUUID())
                .status(BookingStatus.PENDING).totalAmount(BigDecimal.TEN)
                .createdAt(Instant.now().minusSeconds(900)).build();
    }

    @Test
    void databaseOutageIsRetriedOnNextScheduledCycle() {
        var booking = pending();
        var job = new PendingBookingRecoveryJob(recovery);
        ReflectionTestUtils.setField(job, "staleAfterMs", 600_000L);
        when(repository.findPendingCreatedBefore(any()))
                .thenThrow(new DataAccessResourceFailureException("PostgreSQL no disponible"))
                .thenReturn(List.of(booking));
        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        job.recover();
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        job.recover();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(repository, times(2)).findPendingCreatedBefore(any());
        verify(movie).releaseSeat(booking.getScreeningId(), booking.getId());
    }

    @Test
    void failedRemoteReleaseDoesNotBlockOtherPendingBookings() {
        var first = pending();
        var second = pending();
        when(repository.findPendingCreatedBefore(any())).thenReturn(List.of(first, second));
        when(repository.findById(first.getId())).thenReturn(Optional.of(first));
        when(repository.findById(second.getId())).thenReturn(Optional.of(second));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(movie.releaseSeat(first.getScreeningId(), first.getId()))
                .thenThrow(new IllegalStateException("Movie no disponible"));

        assertEquals(2, recovery.recoverCreatedBefore(Instant.now().minusSeconds(600)));
        assertEquals(BookingStatus.CANCELLED, first.getStatus());
        assertEquals(BookingStatus.CANCELLED, second.getStatus());
        verify(movie).releaseSeat(second.getScreeningId(), second.getId());
    }

    @Test
    void failedLocalSaveRemainsEligibleForLaterRecovery() {
        var firstAttempt = pending();
        var secondAttempt = Booking.builder().id(firstAttempt.getId())
                .screeningId(firstAttempt.getScreeningId()).movieTitle(firstAttempt.getMovieTitle())
                .customerId(firstAttempt.getCustomerId()).status(BookingStatus.PENDING)
                .totalAmount(firstAttempt.getTotalAmount()).createdAt(firstAttempt.getCreatedAt()).build();
        when(repository.findPendingCreatedBefore(any())).thenReturn(List.of(firstAttempt));
        when(repository.findById(firstAttempt.getId()))
                .thenReturn(Optional.of(firstAttempt), Optional.of(secondAttempt));
        when(repository.save(any()))
                .thenThrow(new DataAccessResourceFailureException("Error al guardar"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(0, recovery.recoverCreatedBefore(Instant.now().minusSeconds(600)));
        verifyNoInteractions(movie);
        assertEquals(1, recovery.recoverCreatedBefore(Instant.now().minusSeconds(600)));
        assertEquals(BookingStatus.CANCELLED, secondAttempt.getStatus());
        verify(movie).releaseSeat(secondAttempt.getScreeningId(), secondAttempt.getId());
    }

    @Test
    void bookingAlreadyConfirmedIsNotCancelledByRecovery() {
        var candidate = pending();
        var confirmed = Booking.builder().id(candidate.getId()).screeningId(candidate.getScreeningId())
                .status(BookingStatus.CONFIRMED).build();
        when(repository.findPendingCreatedBefore(any())).thenReturn(List.of(candidate));
        when(repository.findById(candidate.getId())).thenReturn(Optional.of(confirmed));

        assertEquals(0, recovery.recoverCreatedBefore(Instant.now().minusSeconds(600)));
        verify(repository, never()).save(any());
        verifyNoInteractions(movie);
    }
}
