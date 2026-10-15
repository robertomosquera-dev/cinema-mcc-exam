package com.mitocode.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "seat_reservations")
@Getter @Setter @NoArgsConstructor
public class SeatReservationJpaEntity {
    @Id private UUID bookingId;
    @Column(nullable = false) private UUID screeningId;
    @Column(nullable = false) private boolean released;
    public SeatReservationJpaEntity(UUID bookingId, UUID screeningId, boolean released) {
        this.bookingId = bookingId;
        this.screeningId = screeningId;
        this.released = released;
    }
}
