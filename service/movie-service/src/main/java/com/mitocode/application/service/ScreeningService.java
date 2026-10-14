package com.mitocode.application.service;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.dto.ScreeningResponse;
import com.mitocode.application.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService implements CreateScreeningUseCase, GetScreeningsUseCase,
        GetScreeningUseCase, ReserveSeatUseCase, ReleaseSeatUseCase {

    private final MovieRepositoryPort movieRepositoryPort;
    private final ScreeningRepositoryPort screeningRepositoryPort;
    private final ScreeningDtoMapper screeningDtoMapper;

    @Override
    @Transactional
    public ScreeningResponse createScreening(CreateScreeningRequest request) {
        if (!movieRepositoryPort.existsById(request.movieId())) {
            throw new ResourceNotFoundException("Película no encontrada: " + request.movieId());
        }
        Screening screening = screeningDtoMapper.toDomain(request);
        screening.validateCapacity();
        screening = screeningRepositoryPort.save(screening);
        return screeningDtoMapper.toResponse(screening);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponse> getScreenings() {
        List<Screening> screenings = screeningRepositoryPort.findAll();
        return screenings.stream()
                .map(screeningDtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningAvailabilityResponse getScreening(UUID screeningId) {
        Screening screening = screeningRepositoryPort.findById(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada: " + screeningId));
        return screeningDtoMapper.toAvailabilityResponse(screening);
    }

    @Override
    @Transactional
    public ReserveSeatResponse reserveSeat(UUID screeningId) {
        Screening screening = screeningRepositoryPort.findByIdForUpdate(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada: " + screeningId));
        screening.reserveSeat();
        Screening savedScreening = screeningRepositoryPort.save(screening);
        return new ReserveSeatResponse(savedScreening.getId(), savedScreening.getAvailableSeats());
    }

    @Override
    @Transactional
    public ReleaseSeatResponse releaseSeat(UUID screeningId) {
        Screening screening = screeningRepositoryPort.findByIdForUpdate(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada: " + screeningId));
        screening.releaseSeat();
        Screening savedScreening = screeningRepositoryPort.save(screening);
        return new ReleaseSeatResponse(savedScreening.getId(), savedScreening.getAvailableSeats());
    }
}
