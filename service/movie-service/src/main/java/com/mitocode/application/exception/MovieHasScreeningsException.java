package com.mitocode.application.exception;

public class MovieHasScreeningsException extends RuntimeException {

    public MovieHasScreeningsException() {
        super("La película tiene funciones asociadas. Desactívala en lugar de eliminarla.");
    }
}
