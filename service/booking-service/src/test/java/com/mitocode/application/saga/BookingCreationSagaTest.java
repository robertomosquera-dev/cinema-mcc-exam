package com.mitocode.application.saga;

import com.mitocode.application.dto.CreateBookingRequest;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.mapper.BookingDtoMapperImpl;
import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.application.saga.step.ConfirmBookingStep;
import com.mitocode.application.saga.step.CreatePendingBookingStep;
import com.mitocode.application.saga.step.ReserveSeatStep;
import com.mitocode.application.saga.step.VerifyScreeningStep;
import com.mitocode.domain.model.Booking;
import com.mitocode.domain.model.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingCreationSagaTest {
    private final UUID screeningId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final UUID bookingId = UUID.randomUUID();
    private MovieClientPort movie;
    private BookingRepositoryPort repository;
    private AtomicReference<Booking> stored;
    private List<IStep> steps;

    @BeforeEach
    void setUp() {
        movie = mock(MovieClientPort.class);
        repository = mock(BookingRepositoryPort.class);
        stored = new AtomicReference<>();
        when(movie.getScreening(screeningId)).thenReturn(
                new ScreeningAvailabilityResponse(screeningId, "Película", 2, true));
        when(repository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            Booking copy = Booking.builder().id(bookingId).screeningId(booking.getScreeningId())
                    .movieTitle(booking.getMovieTitle()).customerId(booking.getCustomerId())
                    .totalAmount(booking.getTotalAmount()).status(booking.getStatus())
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            stored.set(copy);
            return copy;
        });
        when(repository.findById(any())).thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        steps = List.of(new VerifyScreeningStep(movie), new CreatePendingBookingStep(repository),
                new ReserveSeatStep(movie), new ConfirmBookingStep(repository));
    }

    private com.mitocode.application.dto.BookingResponse run() {
        return new SagaStepsExecutor().runSteps(steps,
                new SagaContext(new CreateBookingRequest(screeningId, customerId, new BigDecimal("25.50"))),
                context -> new BookingDtoMapperImpl().toResponse(context.getBooking()));
    }

    @Test
    void successConfirmsOneBookingAndReservesOnce() {
        var response = run();
        assertEquals(BookingStatus.CONFIRMED, response.status());
        assertEquals("Película", response.movieTitle());
        assertEquals(bookingId, response.id());
        verify(movie, times(1)).reserveSeat(screeningId, bookingId);
        verify(movie, never()).releaseSeat(any(), any());
    }

    @Test
    void noSeatsCreatesNoBooking() {
        when(movie.getScreening(screeningId)).thenReturn(
                new ScreeningAvailabilityResponse(screeningId, "Película", 0, true));
        assertThrows(ScreeningUnavailableException.class, this::run);
        verify(repository, never()).save(any());
        verify(movie, never()).reserveSeat(any(), any());
    }

    @Test
    void failedReserveCancelsPendingBooking() {
        var failure = new ScreeningUnavailableException("Sin asientos");
        when(movie.reserveSeat(screeningId, bookingId)).thenThrow(failure);
        assertSame(failure, assertThrows(ScreeningUnavailableException.class, this::run));
        assertEquals(BookingStatus.CANCELLED, stored.get().getStatus());
        verify(movie).releaseSeat(screeningId, bookingId);
    }

    @Test
    void failedConfirmationReleasesSeatAndCancelsBooking() {
        var failure = new IllegalStateException("Fallo al confirmar");
        doThrow(failure).when(repository).save(argThat(b -> b.getStatus() == BookingStatus.CONFIRMED));
        assertSame(failure, assertThrows(IllegalStateException.class, this::run));
        verify(movie).releaseSeat(screeningId, bookingId);
        assertEquals(BookingStatus.CANCELLED, stored.get().getStatus());
    }

    @Test
    void failedReleaseStillCancelsAndPreservesOriginalError() {
        var failure = new IllegalStateException("Fallo al confirmar");
        doThrow(failure).when(repository).save(argThat(b -> b.getStatus() == BookingStatus.CONFIRMED));
        when(movie.releaseSeat(screeningId, bookingId)).thenThrow(new IllegalStateException("Fallo al liberar"));
        assertSame(failure, assertThrows(IllegalStateException.class, this::run));
        assertEquals(BookingStatus.CANCELLED, stored.get().getStatus());
    }

    @Test
    void lostResponseAfterPendingCommitAttemptsCancellationByCandidateId() {
        var failure = new IllegalStateException("Se perdió la respuesta del guardado");
        doAnswer(invocation -> {
            Booking pending = invocation.getArgument(0);
            stored.set(pending);
            throw failure;
        }).when(repository).save(argThat(b -> b.getStatus() == BookingStatus.PENDING));
        assertSame(failure, assertThrows(IllegalStateException.class, this::run));
        assertEquals(BookingStatus.CANCELLED, stored.get().getStatus());
        verify(repository).findById(any());
    }

    @Test
    void responseMappingFailureCompensatesAllCompletedSteps() {
        var failure = new IllegalStateException("Fallo al construir la respuesta");
        var context = new SagaContext(new CreateBookingRequest(screeningId, customerId, BigDecimal.ONE));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> new SagaStepsExecutor().runSteps(steps, context, ignored -> { throw failure; })));
        verify(movie).releaseSeat(screeningId, bookingId);
        assertEquals(BookingStatus.CANCELLED, stored.get().getStatus());
    }
}
