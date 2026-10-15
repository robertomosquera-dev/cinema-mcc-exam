package com.mitocode.application.error;

import java.util.UUID;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(UUID id) {
        super("Reserva no encontrada: " + id);
    }
}
