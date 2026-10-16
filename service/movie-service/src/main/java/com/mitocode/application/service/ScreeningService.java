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
import com.mitocode.application.port.out.SeatReservationPort;
import com.mitocode.domain.model.Screening;
import com.mitocode.domain.model.exception.NoSeatsAvailableException;
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
    private final SeatReservationPort seatReservationPort;
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
    public ReserveSeatResponse reserveSeat(UUID screeningId, UUID bookingId) {
        Screening screening = screeningRepositoryPort.findByIdForUpdate(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada: " + screeningId));
        var existing = seatReservationPort.findByBookingId(bookingId);
        if (existing.isPresent()) {
            var reservation = existing.get();
            if (!reservation.screeningId().equals(screeningId) || reservation.released()) {
                throw new NoSeatsAvailableException();
            }
            return new ReserveSeatResponse(screeningId, screening.getAvailableSeats());
        }
        screening.reserveSeat();
        Screening saved = screeningRepositoryPort.save(screening);
        seatReservationPort.save(bookingId, screeningId, false);
        return new ReserveSeatResponse(screeningId, saved.getAvailableSeats());
    }

    @Override
    @Transactional
    public ReleaseSeatResponse releaseSeat(UUID screeningId, UUID bookingId) {
        Screening screening = screeningRepositoryPort.findByIdForUpdate(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada: " + screeningId));
        var existing = seatReservationPort.findByBookingId(bookingId);
        if (existing.isPresent()) {
            var reservation = existing.get();
            if (!reservation.screeningId().equals(screeningId)) {
                throw new IllegalArgumentException("bookingId asociado con otra función");
            }
            if (!reservation.released()) {
                screening.releaseSeat();
                screening = screeningRepositoryPort.save(screening);
                seatReservationPort.save(bookingId, screeningId, true);
            }
        } else {
            seatReservationPort.save(bookingId, screeningId, true);
        }
        return new ReleaseSeatResponse(screeningId, screening.getAvailableSeats());
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
