package com.mitocode.domain.model.exception;

public class InvalidScreeningCapacityException extends RuntimeException {

    public InvalidScreeningCapacityException() {
        super("Los asientos disponibles deben estar entre cero y la capacidad total positiva.");
    }
}
