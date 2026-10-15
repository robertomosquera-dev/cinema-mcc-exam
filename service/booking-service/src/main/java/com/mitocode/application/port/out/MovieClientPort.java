package com.mitocode.application.port.out;

import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;

import java.util.UUID;

public interface MovieClientPort {

    ScreeningAvailabilityResponse getScreening(UUID screeningId);

    ReserveSeatResponse reserveSeat(UUID screeningId, UUID bookingId);

    ReleaseSeatResponse releaseSeat(UUID screeningId, UUID bookingId);
}
