package com.mitocode.application.port.in;

import com.mitocode.application.dto.MovieResponse;
import com.mitocode.application.dto.UpdateMovieRequest;

import java.util.UUID;

public interface UpdateMovieUseCase {

    MovieResponse updateMovie(UUID movieId, UpdateMovieRequest request);
}
