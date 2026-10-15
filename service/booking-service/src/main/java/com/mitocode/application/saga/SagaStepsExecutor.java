package com.mitocode.application.saga;

import com.mitocode.application.dto.BookingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class SagaStepsExecutor implements ISagaStepsService<IStep, BookingResponse> {

    @Override
    public BookingResponse runSteps(List<IStep> steps, SagaContext context,
                                    Function<SagaContext, BookingResponse> responseFactory) {
        List<IStep> executedSteps = new ArrayList<>();
        for (IStep step : steps) {
            try {
                log.info("Ejecutando paso: {}", step.getClass().getSimpleName());
                step.execute(context);
                executedSteps.add(step);
            } catch (Exception e) {
                log.error("Error en paso {}: {}", step.getClass().getSimpleName(), e.getMessage(), e);
                compensate(executedSteps, context);
                throw new IllegalStateException("Saga fallida", e);
            }
        }

        return responseFactory.apply(context);
    }

    private void compensate(List<IStep> executedSteps, SagaContext context) {
        log.warn("Iniciando compensación...");
        for (int i = executedSteps.size() - 1; i >= 0; i--) {
            IStep step = executedSteps.get(i);
            try {
                log.info("Compensando paso: {}", step.getClass().getSimpleName());
                step.compensate(context);
            } catch (Exception e) {
                log.error("Error compensando {}: {}", step.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
