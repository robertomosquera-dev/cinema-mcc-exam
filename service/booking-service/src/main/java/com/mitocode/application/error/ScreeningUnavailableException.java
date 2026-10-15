package com.mitocode.application.error;

public class ScreeningUnavailableException extends RuntimeException {
    public ScreeningUnavailableException(String message) { super(message); }
    public ScreeningUnavailableException(String message, Throwable cause) { super(message, cause); }
}
