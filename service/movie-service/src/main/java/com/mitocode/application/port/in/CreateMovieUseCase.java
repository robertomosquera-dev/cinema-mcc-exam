package com.mitocode.application.port.in;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.domain.model.Movie;

public interface CreateMovieUseCase {

    Movie createMovie(CreateMovieRequest request);
}
