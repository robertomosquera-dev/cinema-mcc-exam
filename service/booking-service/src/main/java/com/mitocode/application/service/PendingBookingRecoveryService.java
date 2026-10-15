package com.mitocode.application.service;

import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.domain.model.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingBookingRecoveryService {
    private final BookingRepositoryPort bookingRepository;
    private final MovieClientPort movieClient;

    public int recoverCreatedBefore(Instant cutoff) {
        int recovered = 0;
        for (var candidate : bookingRepository.findPendingCreatedBefore(cutoff)) {
            try {
                var current = bookingRepository.findById(candidate.getId()).orElse(null);
                if (current == null || current.getStatus() != BookingStatus.PENDING) {
                    continue;
                }
                current.cancel();
                bookingRepository.save(current);
                recovered++;
                try {
                    movieClient.releaseSeat(current.getScreeningId(), current.getId());
                } catch (RuntimeException e) {
                    log.error("Reserva {} recuperada como CANCELLED, pero no se pudo liberar su asiento",
                            current.getId(), e);
                }
            } catch (RuntimeException e) {
                log.error("No se pudo recuperar la reserva PENDING {}; se reintentará en el próximo ciclo",
                        candidate.getId(), e);
            }
        }
        return recovered;
    }
}
