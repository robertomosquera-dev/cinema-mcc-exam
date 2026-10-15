package com.mitocode.application.saga;

import java.util.List;
import java.util.function.Function;

public interface ISagaStepsService<Steps,T> {
    T runSteps(List<Steps> steps, SagaContext context, Function<SagaContext, T> responseFactory);
}
