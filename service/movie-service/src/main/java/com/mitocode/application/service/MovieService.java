package com.mitocode.application.service;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.application.dto.MovieResponse;
import com.mitocode.application.dto.UpdateMovieRequest;
import com.mitocode.application.exception.MovieHasScreeningsException;
import com.mitocode.application.exception.ResourceNotFoundException;
import com.mitocode.application.mapper.MovieDtoMapper;
import com.mitocode.application.port.in.ActivateMovieUseCase;
import com.mitocode.application.port.in.CreateMovieUseCase;
import com.mitocode.application.port.in.DeactivateMovieUseCase;
import com.mitocode.application.port.in.DeleteMovieUseCase;
import com.mitocode.application.port.in.GetMovieUseCase;
import com.mitocode.application.port.in.GetMoviesUseCase;
import com.mitocode.application.port.in.UpdateMovieUseCase;
import com.mitocode.application.port.out.MovieRepositoryPort;
import com.mitocode.application.port.out.ScreeningRepositoryPort;
import com.mitocode.domain.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService implements CreateMovieUseCase, GetMoviesUseCase, GetMovieUseCase,
        UpdateMovieUseCase, DeleteMovieUseCase, ActivateMovieUseCase, DeactivateMovieUseCase {

    private final MovieRepositoryPort movieRepositoryPort;
    private final ScreeningRepositoryPort screeningRepositoryPort;
    private final MovieDtoMapper movieDtoMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(CreateMovieRequest request) {
        Movie movie = movieDtoMapper.toDomain(request);
        Movie savedMovie = movieRepositoryPort.save(movie);
        return movieDtoMapper.toResponse(savedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getMovies() {
        return movieRepositoryPort.findAll().stream()
                .map(movieDtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovie(UUID movieId) {
        Movie movie = movieRepositoryPort.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada: " + movieId));
        return movieDtoMapper.toResponse(movie);
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(UUID movieId, UpdateMovieRequest request) {
        Movie movie = movieRepositoryPort.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada: " + movieId));
        movie.updateDetails(request.title(), request.director(), request.genre(),
                request.durationMin(), request.rating());
        Movie savedMovie = movieRepositoryPort.save(movie);
        return movieDtoMapper.toResponse(savedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(UUID movieId) {
        if (!movieRepositoryPort.existsById(movieId)) {
            throw new ResourceNotFoundException("Película no encontrada: " + movieId);
        }
        if (screeningRepositoryPort.existsByMovieId(movieId)) {
            throw new MovieHasScreeningsException();
        }
        movieRepositoryPort.deleteById(movieId);
    }

    @Override
    @Transactional
    public MovieResponse activateMovie(UUID movieId) {
        Movie movie = movieRepositoryPort
                .findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada: " + movieId));
        movie.activate();
        movieRepositoryPort.save(movie);
        return movieDtoMapper.toResponse(movie);
    }

    @Override
    @Transactional
    public MovieResponse deactivateMovie(UUID movieId) {
        Movie movie = movieRepositoryPort
                .findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada: " + movieId));
        movie.deactivate();
        movieRepositoryPort.save(movie);
        return movieDtoMapper.toResponse(movie);
    }
}
