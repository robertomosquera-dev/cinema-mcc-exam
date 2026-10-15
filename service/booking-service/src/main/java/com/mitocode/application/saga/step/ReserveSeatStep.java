package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class ReserveSeatStep implements IStep {

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
