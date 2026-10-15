package com.mitocode.application.port.in;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.dto.CreateBookingRequest;

public interface CreateBookingUseCase {

    BookingResponse createBooking(CreateBookingRequest request);
}
