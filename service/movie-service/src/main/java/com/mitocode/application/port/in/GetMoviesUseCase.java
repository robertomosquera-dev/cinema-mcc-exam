package com.mitocode.application.port.in;

import com.mitocode.application.dto.MovieResponse;

import java.util.List;

public interface GetMoviesUseCase {

    List<MovieResponse> getMovies();
}
