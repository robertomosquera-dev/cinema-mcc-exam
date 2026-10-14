package com.mitocode.application.port.in;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.application.dto.MovieResponse;

public interface CreateMovieUseCase {

    MovieResponse createMovie(CreateMovieRequest request);
}
