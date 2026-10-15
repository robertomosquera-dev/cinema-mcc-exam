package com.mitocode.infrastructure.scheduling;

import com.mitocode.application.service.PendingBookingRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingBookingRecoveryJob {
    private final PendingBookingRecoveryService recoveryService;

    @Value("${booking.recovery.stale-after-ms:600000}")
    private long staleAfterMs;

    @Scheduled(fixedDelayString = "${booking.recovery.interval-ms:60000}",
            initialDelayString = "${booking.recovery.initial-delay-ms:60000}")
    public void recover() {
        try {
            int recovered = recoveryService.recoverCreatedBefore(Instant.now().minusMillis(staleAfterMs));
            if (recovered > 0) {
                log.info("Reservas PENDING recuperadas: {}", recovered);
            }
        } catch (RuntimeException e) {
            log.error("No se pudo revisar las reservas PENDING; se reintentará en el próximo ciclo", e);
        }
    }
}
