package com.mitocode.infrastructure.persistence.adapter;

import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.domain.model.Booking;
import com.mitocode.infrastructure.persistence.mapper.BookingPersistenceMapper;
import com.mitocode.infrastructure.persistence.repository.SpringDataBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBookingRepositoryAdapter implements BookingRepositoryPort {

    private final SpringDataBookingRepository repository;
    private final BookingPersistenceMapper mapper;

    @Override
    public Booking save(Booking booking) {
        return mapper.toDomain(repository.save(mapper.toEntity(booking)));
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Booking> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
