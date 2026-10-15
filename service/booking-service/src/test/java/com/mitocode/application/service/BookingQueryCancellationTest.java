package com.mitocode.application.service;

import com.mitocode.application.error.BookingNotFoundException;
import com.mitocode.application.mapper.BookingDtoMapperImpl;
import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.application.saga.SagaStepsExecutor;
import com.mitocode.application.saga.step.ConfirmBookingStep;
import com.mitocode.application.saga.step.CreatePendingBookingStep;
import com.mitocode.application.saga.step.ReserveSeatStep;
import com.mitocode.application.saga.step.VerifyScreeningStep;
import com.mitocode.domain.model.Booking;
import com.mitocode.domain.model.BookingStatus;
import com.mitocode.domain.model.exception.BookingNotCancellableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingQueryCancellationTest {
    private final UUID id = UUID.randomUUID();
    private final UUID screeningId = UUID.randomUUID();
    private BookingRepositoryPort repository;
    private MovieClientPort movie;
    private BookingService service;

    @BeforeEach
    void setUp() {
        repository = mock(BookingRepositoryPort.class);
        movie = mock(MovieClientPort.class);
        service = new BookingService(repository, movie, mock(SagaStepsExecutor.class),
                mock(VerifyScreeningStep.class), mock(CreatePendingBookingStep.class),
                mock(ReserveSeatStep.class), mock(ConfirmBookingStep.class), new BookingDtoMapperImpl());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Booking booking(BookingStatus status) {
        return Booking.builder().id(id).screeningId(screeningId).movieTitle("Título snapshot")
                .customerId(UUID.randomUUID()).status(status).totalAmount(BigDecimal.TEN)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
    }

    @Test
    void getByIdUsesLocalSnapshot() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.CONFIRMED)));
        assertEquals("Título snapshot", service.getBookingById(id).movieTitle());
        verifyNoInteractions(movie);
    }

    @Test
    void getMissingBookingReturnsNotFoundError() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> service.getBookingById(id));
    }

    @Test
    void listMapsLocalBookings() {
        when(repository.findAll()).thenReturn(List.of(booking(BookingStatus.CONFIRMED), booking(BookingStatus.CANCELLED)));
        var responses = service.getBookings();
        assertEquals(2, responses.size());
        assertEquals(BookingStatus.CANCELLED, responses.get(1).status());
        verifyNoInteractions(movie);
    }

    @Test
    void cancelPersistsBeforeReleasingSeat() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.CONFIRMED)));
        assertEquals(BookingStatus.CANCELLED, service.cancelBooking(id).status());
        var order = inOrder(repository, movie);
        order.verify(repository).save(argThat(b -> b.getStatus() == BookingStatus.CANCELLED));
        order.verify(movie).releaseSeat(screeningId, id);
    }

    @Test
    void failedReleasePropagatesErrorAfterLocalCancellation() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.CONFIRMED)));
        var failure = new IllegalStateException("Movie no disponible");
        when(movie.releaseSeat(screeningId, id)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.cancelBooking(id)));
        verify(repository).save(argThat(b -> b.getStatus() == BookingStatus.CANCELLED));
    }

    @Test
    void alreadyCancelledBookingReturnsRuleErrorWithoutReleasing() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.CANCELLED)));
        assertThrows(BookingNotCancellableException.class, () -> service.cancelBooking(id));
        verify(repository, never()).save(any());
        verifyNoInteractions(movie);
    }

    @Test
    void missingBookingCannotBeCancelled() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> service.cancelBooking(id));
        verifyNoInteractions(movie);
    }

    @Test
    void pendingBookingCannotBeCancelledByCustomer() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.PENDING)));
        assertThrows(BookingNotCancellableException.class, () -> service.cancelBooking(id));
        verify(repository, never()).save(any());
        verifyNoInteractions(movie);
    }

    @Test
    void failedLocalSaveDoesNotReleaseSeat() {
        when(repository.findById(id)).thenReturn(Optional.of(booking(BookingStatus.CONFIRMED)));
        doThrow(new DataAccessResourceFailureException("Base de datos no disponible"))
                .when(repository).save(any());
        assertThrows(DataAccessResourceFailureException.class, () -> service.cancelBooking(id));
        verifyNoInteractions(movie);
    }
}
