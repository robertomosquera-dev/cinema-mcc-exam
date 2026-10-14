package com.mitocode.domain.model.exception;

public class NoSeatsAvailableException extends RuntimeException {

    public NoSeatsAvailableException() {
        super("La función seleccionada no tiene asientos disponibles.");
    }
}
