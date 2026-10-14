package com.mitocode.domain.model.exception;

public class InvalidMovieStateException extends RuntimeException {

    public InvalidMovieStateException(String message) {
        super(message);
    }
}
