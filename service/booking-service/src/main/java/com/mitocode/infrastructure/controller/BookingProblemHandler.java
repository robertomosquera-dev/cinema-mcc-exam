package com.mitocode.infrastructure.controller;

import com.mitocode.application.error.MovieServiceException;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.error.BookingNotFoundException;
import com.mitocode.domain.model.exception.BookingNotCancellableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class BookingProblemHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    public ProblemDetail notFound(BookingNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(BookingNotCancellableException.class)
    public ProblemDetail notCancellable(BookingNotCancellableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(ScreeningUnavailableException.class)
    public ProblemDetail unavailable(ScreeningUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(MovieServiceException.class)
    public ProblemDetail movieFailure(MovieServiceException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail databaseFailure(DataAccessException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo guardar la reserva; revise el estado de la base de datos");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalidRequest(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getDefaultMessage()).orElse("Solicitud inválida");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail malformedRequest(HttpMessageNotReadableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Cuerpo de solicitud inválido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail malformedPath(MethodArgumentTypeMismatchException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Identificador inválido");
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail unexpected(RuntimeException exception) {
        log.error("Error inesperado en Booking Service", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo completar la reserva");
    }
}
