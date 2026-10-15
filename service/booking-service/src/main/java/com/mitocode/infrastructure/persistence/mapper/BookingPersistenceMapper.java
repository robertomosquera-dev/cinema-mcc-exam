package com.mitocode.infrastructure.persistence.mapper;

import com.mitocode.domain.model.Booking;
import com.mitocode.infrastructure.persistence.entity.BookingJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingPersistenceMapper {

    Booking toDomain(BookingJpaEntity entity);

    BookingJpaEntity toEntity(Booking booking);
}
