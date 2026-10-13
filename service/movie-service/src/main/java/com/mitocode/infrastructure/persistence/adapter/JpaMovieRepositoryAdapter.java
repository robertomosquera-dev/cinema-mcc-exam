package com.mitocode.infrastructure.persistence.adapter;

import com.mitocode.application.port.out.MovieRepositoryPort;
import com.mitocode.domain.model.Movie;
import com.mitocode.infrastructure.persistence.mapper.MoviePersistenceMapper;
import com.mitocode.infrastructure.persistence.repository.SpringDataMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaMovieRepositoryAdapter implements MovieRepositoryPort {

    private final SpringDataMovieRepository repository;
    private final MoviePersistenceMapper mapper;

    @Override
    public Movie save(Movie movie) {
        return mapper.toDomain(repository.save(mapper.toEntity(movie)));
    }

    @Override
    public Optional<Movie> findById(UUID movieId) {
        return repository.findById(movieId).map(mapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID movieId) {
        repository.deleteById(movieId);
    }
}
