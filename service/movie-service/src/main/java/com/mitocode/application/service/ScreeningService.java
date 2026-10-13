package com.mitocode.application.service;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.dto.ScreeningResponse;
import com.mitocode.application.mapper.ScreeningDtoMapper;
import com.mitocode.application.port.in.CreateScreeningUseCase;
import com.mitocode.application.port.in.GetScreeningUseCase;
import com.mitocode.application.port.in.GetScreeningsUseCase;
import com.mitocode.application.port.in.ReleaseSeatUseCase;
import com.mitocode.application.port.in.ReserveSeatUseCase;
import com.mitocode.application.port.out.MovieRepositoryPort;
import com.mitocode.application.port.out.ScreeningRepositoryPort;
import com.mitocode.domain.model.Screening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScreeningService implements CreateScreeningUseCase, GetScreeningsUseCase,
        GetScreeningUseCase, ReserveSeatUseCase, ReleaseSeatUseCase {

    private final MovieRepositoryPort movieRepositoryPort;
    private final ScreeningRepositoryPort screeningRepositoryPort;
    private final ScreeningDtoMapper screeningDtoMapper;

    @Override
    @Transactional
    public Screening createScreening(CreateScreeningRequest request) {
        // TODO: Comprobar mediante MovieRepositoryPort que la película existe.
        // TODO: Convertir la solicitud a Screening y validar en el dominio sus invariantes.
        // TODO: Persistir la función mediante ScreeningRepositoryPort y devolver el modelo guardado.
        throw new UnsupportedOperationException("Pendiente implementar la creación de funciones");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponse> getScreenings() {
        // TODO: Consultar las funciones y obtener el título de cada película para las respuestas.
        throw new UnsupportedOperationException("Pendiente implementar el listado de funciones");
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningAvailabilityResponse getScreening(UUID screeningId) {
        // TODO: Buscar la función y su película; devolver título y disponibilidad.
        throw new UnsupportedOperationException("Pendiente implementar la consulta de funciones");
    }

    @Override
    @Transactional
    public ReserveSeatResponse reserveSeat(UUID screeningId) {
        // TODO: Buscar la función y reservar un asiento mediante el método de dominio.
        // TODO: Proteger la actualización concurrente, persistir y devolver la disponibilidad.
        throw new UnsupportedOperationException("Pendiente implementar la reserva de asientos");
    }

    @Override
    @Transactional
    public ReleaseSeatResponse releaseSeat(UUID screeningId) {
        // TODO: Buscar la función y liberar un asiento mediante el método de dominio.
        // TODO: Persistir y devolver la disponibilidad actualizada.
        throw new UnsupportedOperationException("Pendiente implementar la liberación de asientos");
    }
}
