package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class CreatePendingBookingStep implements IStep {

    @Override
    public void execute(SagaContext context) {
        // TODO: Guardar el booking PENDING con el título snapshot y asignar bookingId tras guardar.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void compensate(SagaContext context) {
        // TODO: Cancelar localmente el booking si bookingId ya fue asignado.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
