package com.mitocode.application.service;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.application.dto.MovieResponse;
import com.mitocode.application.dto.UpdateMovieRequest;
import com.mitocode.application.mapper.MovieDtoMapper;
import com.mitocode.application.port.in.ActivateMovieUseCase;
import com.mitocode.application.port.in.CreateMovieUseCase;
import com.mitocode.application.port.in.DeactivateMovieUseCase;
import com.mitocode.application.port.in.DeleteMovieUseCase;
import com.mitocode.application.port.in.GetMovieUseCase;
import com.mitocode.application.port.in.GetMoviesUseCase;
import com.mitocode.application.port.in.UpdateMovieUseCase;
import com.mitocode.application.port.out.MovieRepositoryPort;
import com.mitocode.domain.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService implements CreateMovieUseCase, GetMoviesUseCase, GetMovieUseCase,
        UpdateMovieUseCase, DeleteMovieUseCase, ActivateMovieUseCase, DeactivateMovieUseCase {

    private final MovieRepositoryPort movieRepositoryPort;
    private final MovieDtoMapper movieDtoMapper;

    @Override
    @Transactional
    public Movie createMovie(CreateMovieRequest request) {
        // TODO: Convertir la solicitud a Movie mediante el mapper de aplicación.
        // TODO: Persistir la película mediante MovieRepositoryPort y devolver el modelo guardado.
        throw new UnsupportedOperationException("Pendiente implementar la creación de películas");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getMovies() {
        // TODO: Consultar las películas mediante MovieRepositoryPort y mapearlas a respuestas.
        throw new UnsupportedOperationException("Pendiente implementar el listado de películas");
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovie(UUID movieId) {
        // TODO: Buscar la película por id, resolver el caso inexistente y mapear la respuesta.
        throw new UnsupportedOperationException("Pendiente implementar la consulta de películas");
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(UUID movieId, UpdateMovieRequest request) {
        // TODO: Buscar la película y aplicar los cambios mediante métodos del dominio.
        // TODO: Persistir los cambios y mapear la respuesta.
        throw new UnsupportedOperationException("Pendiente implementar la actualización de películas");
    }

    @Override
    @Transactional
    public void deleteMovie(UUID movieId) {
        // TODO: Comprobar que la película existe y eliminarla mediante MovieRepositoryPort.
        throw new UnsupportedOperationException("Pendiente implementar la eliminación de películas");
    }

    @Override
    @Transactional
    public MovieResponse activateMovie(UUID movieId) {
        // TODO: Buscar la película, activarla en el dominio, persistir y mapear la respuesta.
        throw new UnsupportedOperationException("Pendiente implementar la activación de películas");
    }

    @Override
    @Transactional
    public MovieResponse deactivateMovie(UUID movieId) {
        // TODO: Buscar la película, desactivarla en el dominio, persistir y mapear la respuesta.
        throw new UnsupportedOperationException("Pendiente implementar la desactivación de películas");
    }
}
