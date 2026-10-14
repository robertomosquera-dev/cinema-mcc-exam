package com.mitocode.infrastructure.persistence.adapter;

import com.mitocode.application.port.out.ScreeningRepositoryPort;
import com.mitocode.domain.model.Screening;
import com.mitocode.infrastructure.persistence.mapper.ScreeningPersistenceMapper;
import com.mitocode.infrastructure.persistence.repository.SpringDataMovieRepository;
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
    private final SpringDataMovieRepository movieRepository;
    private final ScreeningPersistenceMapper mapper;

    @Override
    public Screening save(Screening screening) {
        var entity = mapper.toEntity(screening);
        entity.setMovie(movieRepository.getReferenceById(screening.getMovie().getId()));
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Screening> findById(UUID screeningId) {
        return repository.findById(screeningId).map(mapper::toDomain);
    }

    @Override
    public Optional<Screening> findByIdForUpdate(UUID screeningId) {
        return repository.findByIdForUpdate(screeningId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByMovieId(UUID movieId) {
        return repository.existsByMovie_Id(movieId);
    }

    @Override
    public List<Screening> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
