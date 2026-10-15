package com.mitocode.application.port.out;

import com.mitocode.domain.model.Booking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepositoryPort {

    Booking save(Booking booking);

    Optional<Booking> findById(UUID id);
}
