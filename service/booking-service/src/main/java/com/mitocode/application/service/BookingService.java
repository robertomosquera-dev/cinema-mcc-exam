package com.mitocode.application.service;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.dto.CreateBookingRequest;
import com.mitocode.application.port.in.CancelBookingUseCase;
import com.mitocode.application.port.in.CreateBookingUseCase;
import com.mitocode.application.port.in.GetBookingByIdUseCase;
import com.mitocode.application.port.in.GetBookingsUseCase;
import com.mitocode.application.mapper.BookingDtoMapper;
import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import com.mitocode.application.saga.SagaStepsExecutor;
import com.mitocode.application.saga.step.VerifyScreeningStep;
import com.mitocode.application.saga.step.CreatePendingBookingStep;
import com.mitocode.application.saga.step.ReserveSeatStep;
import com.mitocode.application.saga.step.ConfirmBookingStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService implements CreateBookingUseCase, GetBookingByIdUseCase,
        GetBookingsUseCase, CancelBookingUseCase {
    private final SagaStepsExecutor saga;
    private final VerifyScreeningStep verifyScreeningStep;
    private final CreatePendingBookingStep createPendingBookingStep;
    private final ReserveSeatStep reserveSeatStep;
    private final ConfirmBookingStep confirmBookingStep;
    private final BookingDtoMapper mapper;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        SagaContext context = new SagaContext(request);
        List<IStep> steps = List.of(verifyScreeningStep, createPendingBookingStep,
                reserveSeatStep, confirmBookingStep);
        return saga.runSteps(steps, context, current -> mapper.toResponse(current.getBooking()));
    }

    @Override
    public BookingResponse getBookingById(UUID id) {
        // TODO: Consultar la reserva local por su identificador.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public List<BookingResponse> getBookings() {
        // TODO: Consultar y devolver las reservas locales.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public BookingResponse cancelBooking(UUID id) {
        // TODO: Coordinar la cancelación de la reserva cuando se implemente la Saga.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
