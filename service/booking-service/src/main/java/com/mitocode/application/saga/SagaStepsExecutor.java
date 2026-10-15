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
        List<IStep> startedSteps = new ArrayList<>();
        try {
            for (IStep step : steps) {
                log.info("Ejecutando paso: {}", step.getClass().getSimpleName());
                startedSteps.add(step);
                step.execute(context);
            }
            return responseFactory.apply(context);
        } catch (RuntimeException e) {
            log.error("Saga fallida: {}", e.getMessage(), e);
            compensate(startedSteps, context);
            throw e;
        }
    }

    private void compensate(List<IStep> startedSteps, SagaContext context) {
        log.warn("Iniciando compensación...");
        for (int i = startedSteps.size() - 1; i >= 0; i--) {
            IStep step = startedSteps.get(i);
            try {
                log.info("Compensando paso: {}", step.getClass().getSimpleName());
                step.compensate(context);
            } catch (Exception e) {
                log.error("Error compensando {}: {}", step.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
