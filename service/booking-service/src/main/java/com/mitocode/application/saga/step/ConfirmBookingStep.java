package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import com.mitocode.application.port.out.BookingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmBookingStep implements IStep {
    private final BookingRepositoryPort repository;

    @Override
    public void execute(SagaContext context) {
        context.getBooking().confirm();
        context.setBooking(repository.save(context.getBooking()));
    }

    @Override
    public void compensate(SagaContext context) {
        // La cancelación local la realiza CreatePendingBookingStep en orden inverso.
    }
}
