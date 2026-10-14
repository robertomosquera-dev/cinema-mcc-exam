package com.mitocode.infrastructure.persistence.mapper;

import com.mitocode.domain.model.Movie;
import com.mitocode.infrastructure.persistence.entity.MovieJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoviePersistenceMapper {

    @Mapping(target = "screenings", ignore = true)
    Movie toDomain(MovieJpaEntity entity);

    @Mapping(target = "screenings", ignore = true)
    MovieJpaEntity toEntity(Movie movie);
}
