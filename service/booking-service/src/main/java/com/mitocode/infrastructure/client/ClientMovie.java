package com.mitocode.infrastructure.client;

import com.mitocode.application.dto.ReleaseSeatResponse;
import com.mitocode.application.dto.ReserveSeatResponse;
import com.mitocode.application.dto.ScreeningAvailabilityResponse;
import com.mitocode.application.port.out.MovieClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientMovie implements MovieClientPort {

    private final WebClient movieServiceWebClient;

    @Override
    public ScreeningAvailabilityResponse getScreening(UUID screeningId) {
        return movieServiceWebClient.get()
                .uri("/screenings/{id}", screeningId)
                .retrieve()
                .bodyToMono(ScreeningAvailabilityResponse.class)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Movie Service no devolvió la función"));
    }

    @Override
    public ReserveSeatResponse reserveSeat(UUID screeningId) {
        return movieServiceWebClient.patch()
                .uri("/screenings/{id}/reserve", screeningId)
                .retrieve()
                .bodyToMono(ReserveSeatResponse.class)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Movie Service no confirmó la reserva del asiento"));
    }

    @Override
    public ReleaseSeatResponse releaseSeat(UUID screeningId) {
        return movieServiceWebClient.patch()
                .uri("/screenings/{id}/release", screeningId)
                .retrieve()
                .bodyToMono(ReleaseSeatResponse.class)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Movie Service no confirmó la liberación del asiento"));
    }

}
