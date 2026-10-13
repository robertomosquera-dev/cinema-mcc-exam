package com.mitocode.infrastructure.persistence.adapter;

import com.mitocode.application.port.out.ScreeningRepositoryPort;
import com.mitocode.domain.model.Screening;
import com.mitocode.infrastructure.persistence.mapper.ScreeningPersistenceMapper;
import com.mitocode.infrastructure.persistence.repository.SpringDataScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaScreeningRepositoryAdapter implements ScreeningRepositoryPort {

    private final SpringDataScreeningRepository repository;
    private final ScreeningPersistenceMapper mapper;

    @Override
    public Screening save(Screening screening) {
        return mapper.toDomain(repository.save(mapper.toEntity(screening)));
    }

    @Override
    public Optional<Screening> findById(UUID screeningId) {
        return repository.findById(screeningId).map(mapper::toDomain);
    }

    @Override
    public List<Screening> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
