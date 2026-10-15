package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class VerifyScreeningStep implements IStep {

    @Override
    public void execute(SagaContext context) {
        // TODO: Consultar Movie y verificar disponibilidad antes de crear el booking.
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void compensate(SagaContext context) {
        // La consulta no modifica estado y no requiere compensación.
    }
}
