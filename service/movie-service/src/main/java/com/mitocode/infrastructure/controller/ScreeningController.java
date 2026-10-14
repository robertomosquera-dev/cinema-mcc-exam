package com.mitocode.infrastructure.controller;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.dto.ScreeningResponse;
import com.mitocode.application.port.in.CreateScreeningUseCase;
import com.mitocode.application.port.in.GetScreeningUseCase;
import com.mitocode.application.port.in.GetScreeningsUseCase;
import com.mitocode.application.port.in.ReleaseSeatUseCase;
import com.mitocode.application.port.in.ReserveSeatUseCase;
import com.mitocode.infrastructure.utils.ResourceUriUtils;
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
@RequestMapping(ScreeningController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Funciones")
public class ScreeningController {

    public static final String BASE_PATH = "/api/v1/screenings";
    private static final String RESOURCE_PATH = BASE_PATH + "/{id}";

    private final CreateScreeningUseCase createScreeningUseCase;
    private final GetScreeningsUseCase getScreeningsUseCase;
    private final GetScreeningUseCase getScreeningUseCase;
    private final ReserveSeatUseCase reserveSeatUseCase;
    private final ReleaseSeatUseCase releaseSeatUseCase;

    @PostMapping
    @Operation(summary = "Crear función")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Función creada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Película inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<ScreeningResponse> create(@Valid @RequestBody CreateScreeningRequest request,
                                                    UriComponentsBuilder uriBuilder) {
        ScreeningResponse response = createScreeningUseCase.createScreening(request);
        return ResponseEntity.created(ResourceUriUtils.created(uriBuilder, RESOURCE_PATH, response.id()))
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar funciones")
    @ApiResponse(responseCode = "200", description = "Funciones encontradas")
    public ResponseEntity<List<ScreeningResponse>> findAll() {
        return ResponseEntity.ok(getScreeningsUseCase.getScreenings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar función y disponibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Función encontrada"),
            @ApiResponse(responseCode = "404", description = "Función inexistente")
    })
    public ResponseEntity<ScreeningAvailabilityResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(getScreeningUseCase.getScreening(id));
    }

    @PatchMapping("/{id}/reserve")
    @Operation(summary = "Reservar un asiento", description = "Descuenta un asiento disponible; Booking lo utiliza durante la Saga de reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento reservado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Función inexistente"),
            @ApiResponse(responseCode = "422", description = "Asientos no disponibles")
    })
    public ResponseEntity<ReserveSeatResponse> reserve(@PathVariable UUID id) {
        return ResponseEntity.ok(reserveSeatUseCase.reserveSeat(id));
    }

    @PatchMapping("/{id}/release")
    @Operation(summary = "Liberar un asiento", description = "Devuelve un asiento tras cancelación o compensación de la Saga coordinada por Booking.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento liberado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Función inexistente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public ResponseEntity<ReleaseSeatResponse> release(@PathVariable UUID id) {
        return ResponseEntity.ok(releaseSeatUseCase.releaseSeat(id));
    }
}
