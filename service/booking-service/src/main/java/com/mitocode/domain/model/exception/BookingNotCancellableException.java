package com.mitocode.domain.model.exception;

public class BookingNotCancellableException extends RuntimeException {
    public BookingNotCancellableException(String message) {
        super(message);
    }
}
