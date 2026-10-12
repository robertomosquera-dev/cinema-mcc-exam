package com.mitocode.movie.infrastructure.persistence.adapter;

import com.mitocode.movie.application.port.out.MovieRepositoryPort;
import com.mitocode.movie.infrastructure.persistence.repository.SpringDataMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaMovieRepositoryAdapter implements MovieRepositoryPort {

    private final SpringDataMovieRepository repository;
}
