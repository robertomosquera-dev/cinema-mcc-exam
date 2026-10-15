package com.mitocode.application.port.in;

import com.mitocode.application.dto.BookingResponse;

import java.util.UUID;

public interface CancelBookingUseCase {

    BookingResponse cancelBooking(UUID id);
}
