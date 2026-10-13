package com.mitocode.application.port.in;

import java.util.UUID;

public interface DeleteMovieUseCase {

    void deleteMovie(UUID movieId);
}
