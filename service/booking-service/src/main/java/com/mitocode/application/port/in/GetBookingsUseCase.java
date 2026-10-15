package com.mitocode.application.port.in;

import com.mitocode.application.dto.BookingResponse;

import java.util.List;

public interface GetBookingsUseCase {

    List<BookingResponse> getBookings();
}
