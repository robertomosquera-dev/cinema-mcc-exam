package com.mitocode.application.port.in;

import com.mitocode.application.dto.BookingResponse;

import java.util.UUID;

public interface GetBookingByIdUseCase {

    BookingResponse getBookingById(UUID id);
}
