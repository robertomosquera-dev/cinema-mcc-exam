package com.mitocode.application.port.in;

import com.mitocode.application.dto.MovieResponse;

import java.util.UUID;

public interface ActivateMovieUseCase {

    MovieResponse activateMovie(UUID movieId);
}
