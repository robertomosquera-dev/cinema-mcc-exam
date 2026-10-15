package com.mitocode.domain.model;

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

    // TODO: Incorporar aquí las reglas de confirmación y cancelación de la reserva.
}
