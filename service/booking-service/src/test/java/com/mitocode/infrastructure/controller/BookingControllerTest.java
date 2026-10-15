package com.mitocode.infrastructure.controller;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.error.ScreeningUnavailableException;
import com.mitocode.application.error.BookingNotFoundException;
import com.mitocode.application.port.in.CancelBookingUseCase;
import com.mitocode.application.port.in.CreateBookingUseCase;
import com.mitocode.application.port.in.GetBookingByIdUseCase;
import com.mitocode.application.port.in.GetBookingsUseCase;
import com.mitocode.domain.model.BookingStatus;
import com.mitocode.domain.model.exception.BookingNotCancellableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest {
    private final CreateBookingUseCase create = mock(CreateBookingUseCase.class);
    private final GetBookingByIdUseCase getById = mock(GetBookingByIdUseCase.class);
    private final GetBookingsUseCase list = mock(GetBookingsUseCase.class);
    private final CancelBookingUseCase cancel = mock(CancelBookingUseCase.class);
    private final MockMvc mvc;

    BookingControllerTest() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new BookingController(create, getById, list, cancel))
                .setControllerAdvice(new BookingProblemHandler()).setValidator(validator).build();
    }

    @Test
    void createdHasLocationAndConfirmedResponse() throws Exception {
        UUID id = UUID.randomUUID();
        when(create.createBooking(any())).thenReturn(new BookingResponse(id, UUID.randomUUID(),
                "Película", UUID.randomUUID(), BookingStatus.CONFIRMED, BigDecimal.ONE,
                Instant.now(), Instant.now()));
        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"screeningId\":\"" + UUID.randomUUID() + "\",\"customerId\":\""
                                + UUID.randomUUID() + "\",\"totalAmount\":1}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/bookings/" + id))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void invalidRequestReturnsProblemDetail400() throws Exception {
        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"totalAmount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(create);
    }

    @Test
    void unavailableScreeningKeeps422ProblemDetail() throws Exception {
        when(create.createBooking(any())).thenThrow(new ScreeningUnavailableException("Sin asientos"));
        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"screeningId\":\"" + UUID.randomUUID() + "\",\"customerId\":\""
                                + UUID.randomUUID() + "\",\"totalAmount\":1}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Sin asientos"));
    }

    @Test
    void getAndListReturnBookings() throws Exception {
        UUID id = UUID.randomUUID();
        var response = response(id, BookingStatus.CONFIRMED);
        when(getById.getBookingById(id)).thenReturn(response);
        when(list.getBookings()).thenReturn(List.of(response));
        mvc.perform(get("/api/v1/bookings/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id.toString()));
        mvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    void cancelReturnsBookingAndBusinessErrors() throws Exception {
        UUID id = UUID.randomUUID();
        when(cancel.cancelBooking(id)).thenReturn(response(id, BookingStatus.CANCELLED))
                .thenThrow(new BookingNotCancellableException("La reserva ya está cancelada"));
        mvc.perform(patch("/api/v1/bookings/{id}/cancel", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));
        mvc.perform(patch("/api/v1/bookings/{id}/cancel", id))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void missingBookingReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(getById.getBookingById(id)).thenThrow(new BookingNotFoundException(id));
        mvc.perform(get("/api/v1/bookings/{id}", id))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void malformedBookingIdReturns400() throws Exception {
        mvc.perform(get("/api/v1/bookings/no-es-uuid"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    private BookingResponse response(UUID id, BookingStatus status) {
        return new BookingResponse(id, UUID.randomUUID(), "Película", UUID.randomUUID(),
                status, BigDecimal.ONE, Instant.now(), Instant.now());
    }
}
