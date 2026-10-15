package com.mitocode.application.error;

public class MovieServiceException extends RuntimeException {
    public MovieServiceException(String message, Throwable cause) { super(message, cause); }
}
