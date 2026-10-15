package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmBookingStep implements IStep {

    @Override
    public void execute(SagaContext context) {
        // TODO: Confirmar mediante el dominio y guardar el booking CONFIRMED.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void compensate(SagaContext context) {
        // TODO: Definir la compensación si se agregan pasos posteriores a la confirmación.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
