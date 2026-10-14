package com.mitocode.domain.model.exception;

public class NoActiveScreeningException extends RuntimeException {
    public NoActiveScreeningException() {
        super("La función no está activa.");
    }
}
