package com.mitocode.infrastructure.persistence.mapper;

import com.mitocode.domain.model.Movie;
import com.mitocode.infrastructure.persistence.entity.MovieJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MoviePersistenceMapper {

    Movie toDomain(MovieJpaEntity entity);

    MovieJpaEntity toEntity(Movie movie);
}
