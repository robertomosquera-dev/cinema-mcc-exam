package com.mitocode.application.port.out;

import com.mitocode.domain.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovieRepositoryPort {

    Movie save(Movie movie);

    Optional<Movie> findById(UUID movieId);

    List<Movie> findAll();

    void deleteById(UUID movieId);
}
