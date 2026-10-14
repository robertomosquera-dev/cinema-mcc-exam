package com.mitocode.application.port.out;

import com.mitocode.domain.model.Screening;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScreeningRepositoryPort {

    Screening save(Screening screening);

    Optional<Screening> findById(UUID screeningId);

    Optional<Screening> findByIdForUpdate(UUID screeningId);

    boolean existsByMovieId(UUID movieId);

    List<Screening> findAll();
}
