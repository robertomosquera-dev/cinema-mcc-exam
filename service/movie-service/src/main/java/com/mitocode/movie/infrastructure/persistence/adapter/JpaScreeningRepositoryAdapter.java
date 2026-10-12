package com.mitocode.movie.infrastructure.persistence.adapter;

import com.mitocode.movie.application.port.out.ScreeningRepositoryPort;
import com.mitocode.movie.infrastructure.persistence.repository.SpringDataScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaScreeningRepositoryAdapter implements ScreeningRepositoryPort {

    private final SpringDataScreeningRepository repository;
}
