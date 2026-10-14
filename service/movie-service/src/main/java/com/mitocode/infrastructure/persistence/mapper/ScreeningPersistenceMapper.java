package com.mitocode.infrastructure.persistence.mapper;

import com.mitocode.domain.model.Screening;
import com.mitocode.infrastructure.persistence.entity.ScreeningJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MoviePersistenceMapper.class)
public interface ScreeningPersistenceMapper {

    Screening toDomain(ScreeningJpaEntity entity);

    @Mapping(target = "movie", ignore = true)
    ScreeningJpaEntity toEntity(Screening screening);
}
