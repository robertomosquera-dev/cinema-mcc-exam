package com.mitocode.infrastructure.controller;

import com.mitocode.application.exception.MovieHasScreeningsException;
import com.mitocode.application.exception.ResourceNotFoundException;
import com.mitocode.domain.model.exception.InvalidMovieStateException;
import com.mitocode.domain.model.exception.InvalidScreeningCapacityException;
import com.mitocode.domain.model.exception.NoActiveScreeningException;
import com.mitocode.domain.model.exception.NoSeatsAvailableException;
import com.mitocode.domain.model.exception.SeatsAlreadyFullException;
import com.mitocode.infrastructure.utils.ProblemDetailUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException exception,
                                        HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler(NoSeatsAvailableException.class)
    public ProblemDetail handleNoSeatsAvailable(NoSeatsAvailableException exception,
                                                HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Asientos no disponibles",
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler(MovieHasScreeningsException.class)
    public ProblemDetail handleMovieHasScreenings(MovieHasScreeningsException exception,
                                                  HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Película con funciones",
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler({NoActiveScreeningException.class, SeatsAlreadyFullException.class,
            InvalidMovieStateException.class, InvalidScreeningCapacityException.class})
    public ProblemDetail handleBusinessRule(RuntimeException exception,
                                            HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Regla de negocio incumplida",
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException exception,
                                             HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Restricción de datos",
                "La operación viola una restricción de datos relacionados.",
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception,
                                          HttpServletRequest request) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("La solicitud contiene datos inválidos.");

        return ProblemDetailUtils.create(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                detail,
                URI.create(request.getRequestURI())
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail handleMalformedRequest(Exception exception,
                                                HttpServletRequest request) {
        return ProblemDetailUtils.create(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "El cuerpo o un parámetro de la solicitud tiene un formato inválido.",
                URI.create(request.getRequestURI())
        );
    }
}
