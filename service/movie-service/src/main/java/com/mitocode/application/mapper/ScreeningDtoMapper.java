package com.mitocode.application.mapper;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.dto.ScreeningResponse;
import com.mitocode.domain.model.Movie;
import com.mitocode.domain.model.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ScreeningDtoMapper {


    @Mapping(target = "movie", source = "movieId")
    Screening toDomain(CreateScreeningRequest request);

    @Mapping(target = "movieId", source = "screening.movie.id")
    @Mapping(target = "movieTitle", source = "screening.movie.title")
    ScreeningResponse toResponse(Screening screening);

    @Mapping(target = "movieTitle", source = "screening.movie.title")
    ScreeningAvailabilityResponse toAvailabilityResponse(Screening screening);

    default Movie newMovie(UUID id) {
        return id == null ? null : new Movie(id);
    }

}
