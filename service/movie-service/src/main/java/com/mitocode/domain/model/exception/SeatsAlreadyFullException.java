package com.mitocode.domain.model.exception;

public class SeatsAlreadyFullException extends RuntimeException {

    public SeatsAlreadyFullException() {
        super("La función ya tiene todos sus asientos disponibles.");
    }
}
