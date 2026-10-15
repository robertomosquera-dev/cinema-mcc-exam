package com.mitocode.domain.model;

import com.mitocode.domain.model.exception.BookingNotCancellableException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Booking {

    private UUID id;

    private UUID screeningId;

    private String movieTitle;

    private UUID customerId;

    private BookingStatus status;

    private BigDecimal totalAmount;

    private Instant createdAt;

    private Instant updatedAt;

    public static Booking pending(UUID screeningId, String movieTitle, UUID customerId, BigDecimal totalAmount) {
        if (screeningId == null || customerId == null || movieTitle == null || movieTitle.isBlank()
                || totalAmount == null || totalAmount.signum() <= 0) {
            throw new IllegalArgumentException("Los datos de la reserva son inválidos");
        }
        return Booking.builder()
                .id(UUID.randomUUID())
                .screeningId(screeningId)
                .movieTitle(movieTitle)
                .customerId(customerId)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .build();
    }

    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Solo se puede confirmar una reserva pendiente");
        }
        status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            return;
        }
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("No se puede cancelar una reserva sin estado válido");
        }
        status = BookingStatus.CANCELLED;
    }

    public void cancelByCustomer() {
        if (status == BookingStatus.CANCELLED) {
            throw new BookingNotCancellableException("La reserva ya está cancelada");
        }
        if (status != BookingStatus.CONFIRMED) {
            throw new BookingNotCancellableException("Solo se puede cancelar una reserva confirmada");
        }
        cancel();
    }
}
