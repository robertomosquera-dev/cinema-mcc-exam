package com.mitocode.domain.model;

import com.mitocode.domain.model.exception.InvalidMovieStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieTest {

    @Test
    void movieStartsActiveAndStateChangesRejectDuplicates() {
        Movie movie = Movie.builder().build();

        assertTrue(movie.isActive());
        assertThrows(InvalidMovieStateException.class, movie::activate);

        movie.deactivate();
        assertFalse(movie.isActive());
        assertThrows(InvalidMovieStateException.class, movie::deactivate);

        movie.activate();
        assertTrue(movie.isActive());
    }
}
