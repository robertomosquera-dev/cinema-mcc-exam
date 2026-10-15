package com.mitocode.infrastructure.controller;

import com.mitocode.application.dto.BookingResponse;
import com.mitocode.application.dto.CreateBookingRequest;
import com.mitocode.application.port.in.CancelBookingUseCase;
import com.mitocode.application.port.in.CreateBookingUseCase;
import com.mitocode.application.port.in.GetBookingByIdUseCase;
import com.mitocode.application.port.in.GetBookingsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(BookingController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Reservas")
public class BookingController {

    public static final String BASE_PATH = "/api/v1/bookings";
    private static final String RESOURCE_PATH = BASE_PATH + "/{id}";

    private final CreateBookingUseCase createBookingUseCase;
    private final GetBookingByIdUseCase getBookingByIdUseCase;
    private final GetBookingsUseCase getBookingsUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;

    @PostMapping
    @Operation(summary = "Crear reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        BookingResponse response = createBookingUseCase.createBooking(request);
        return ResponseEntity.created(uriBuilder.path(RESOURCE_PATH).buildAndExpand(response.id()).toUri())
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar reservas")
    @ApiResponse(responseCode = "200", description = "Reservas encontradas")
    public ResponseEntity<List<BookingResponse>> findAll() {
        return ResponseEntity.ok(getBookingsUseCase.getBookings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    public ResponseEntity<BookingResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(getBookingByIdUseCase.getBookingById(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<BookingResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(cancelBookingUseCase.cancelBooking(id));
    }
}
