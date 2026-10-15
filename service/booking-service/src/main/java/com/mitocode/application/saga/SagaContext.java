package com.mitocode.application.saga;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.dto.CreateBookingRequest;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.domain.model.Booking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class SagaContext {
    private final CreateBookingRequest request;
    private ScreeningAvailabilityResponse screeningAvailabilityResponse;
    private Booking booking;
    private UUID pendingCandidateId;
    private UUID bookingId = null;
    private boolean reserveAttempted = false;
    private boolean seatReserved = false;
    private BookingResponse bookingResponse;
}
