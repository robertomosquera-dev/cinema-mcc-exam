package com.mitocode.application.saga.step;

import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import com.mitocode.application.error.ScreeningUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyScreeningStep implements IStep {

    private final MovieClientPort movieClientPort;

    @Override
    public void execute(SagaContext context) {
        try {
            var screening = movieClientPort.getScreening(context.getRequest().screeningId());
            if (screening == null || !screening.active() || screening.availableSeats() == null
                    || screening.availableSeats() < 1 || screening.movieTitle() == null
                    || screening.movieTitle().isBlank()) {
                throw new ScreeningUnavailableException("La función no está disponible o no tiene asientos");
            }
            context.setScreeningAvailabilityResponse(screening);
        } catch (RuntimeException e) {
            if (e instanceof ScreeningUnavailableException) throw e;
            throw new ScreeningUnavailableException("No se pudo verificar la disponibilidad de la función", e);
        }
    }

    @Override
    public void compensate(SagaContext context) {
        // La consulta no modifica estado y no requiere compensación.
    }
}
