package com.mitocode.application.port.in;

import com.mitocode.application.dto.ReserveSeatResponse;

import java.util.UUID;

public interface ReserveSeatUseCase {

    ReserveSeatResponse reserveSeat(UUID screeningId);

    ReserveSeatResponse reserveSeat(UUID screeningId, UUID bookingId);
}
