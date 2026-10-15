package com.mitocode.application.mapper;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.domain.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingDtoMapper {

    BookingResponse toResponse(Booking booking);
}
