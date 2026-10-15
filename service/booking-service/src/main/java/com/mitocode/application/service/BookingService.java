package com.mitocode.application.service;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.dto.CreateBookingRequest;
import com.mitocode.application.port.in.CancelBookingUseCase;
import com.mitocode.application.port.in.CreateBookingUseCase;
import com.mitocode.application.port.in.GetBookingByIdUseCase;
import com.mitocode.application.port.in.GetBookingsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService implements CreateBookingUseCase, GetBookingByIdUseCase,
        GetBookingsUseCase, CancelBookingUseCase {

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        // TODO: Coordinar la creación de la reserva cuando se implemente la Saga.
        throw new UnsupportedOperationException("Pendiente de implementación");
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
