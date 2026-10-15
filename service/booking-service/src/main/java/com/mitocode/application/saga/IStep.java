package com.mitocode.application.saga;

public interface IStep {
    void execute(SagaContext context);
    void compensate(SagaContext context);
}
