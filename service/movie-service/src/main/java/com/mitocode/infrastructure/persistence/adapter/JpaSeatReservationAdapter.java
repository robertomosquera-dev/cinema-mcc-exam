package com.mitocode.infrastructure.persistence.adapter;

import com.mitocode.application.port.out.SeatReservationPort;
import com.mitocode.infrastructure.persistence.entity.SeatReservationJpaEntity;
import com.mitocode.infrastructure.persistence.repository.SpringDataSeatReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaSeatReservationAdapter implements SeatReservationPort {
    private final SpringDataSeatReservationRepository repository;
    @Override
    public Optional<SeatReservation> findByBookingId(UUID bookingId) {
        return repository.findById(bookingId)
                .map(e -> new SeatReservation(e.getBookingId(), e.getScreeningId(), e.isReleased()));
    }
    @Override
    public void save(UUID bookingId, UUID screeningId, boolean released) {
        repository.save(new SeatReservationJpaEntity(bookingId, screeningId, released));
    }
}
