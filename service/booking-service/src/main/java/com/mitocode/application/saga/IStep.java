package com.mitocode.application.saga;

public interface IStep {
    void execute(SagaContext context) throws Exception;
    void compensate(SagaContext context);
}
