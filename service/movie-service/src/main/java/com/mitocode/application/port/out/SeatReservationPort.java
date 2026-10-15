package com.mitocode.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface SeatReservationPort {
    Optional<SeatReservation> findByBookingId(UUID bookingId);
    void save(UUID bookingId, UUID screeningId, boolean released);
    record SeatReservation(UUID bookingId, UUID screeningId, boolean released) {}
}
