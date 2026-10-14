package com.mitocode.application.mapper;

import com.mitocode.application.dto.CreateMovieRequest;
import com.mitocode.application.dto.MovieResponse;
import com.mitocode.domain.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface MovieDtoMapper {
    Movie toDomain(CreateMovieRequest request);
    MovieResponse toResponse(Movie movie);
}
