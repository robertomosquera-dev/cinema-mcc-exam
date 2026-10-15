package com.mitocode.application.saga.step;

import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveSeatStep implements IStep {

    private final MovieClientPort movieClientPort;

    @Override
    public void execute(SagaContext context) {
        // TODO: Solicitar la reserva a Movie y marcar seatReserved solo al recibir éxito.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void compensate(SagaContext context) {
        // TODO: Liberar el asiento en Movie solo si seatReserved es true.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
