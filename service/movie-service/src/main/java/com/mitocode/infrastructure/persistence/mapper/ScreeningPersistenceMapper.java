package com.mitocode.infrastructure.persistence.mapper;

import com.mitocode.domain.model.Screening;
import com.mitocode.infrastructure.persistence.entity.ScreeningJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScreeningPersistenceMapper {

    Screening toDomain(ScreeningJpaEntity entity);

    ScreeningJpaEntity toEntity(Screening screening);
}
