package com.mitocode.application.saga.step;

import com.mitocode.application.port.out.MovieClientPort;
import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveSeatStep implements IStep {

    private final MovieClientPort movieClientPort;

    @Override
    public void execute(SagaContext context) {
        context.setReserveAttempted(true);
        movieClientPort.reserveSeat(context.getRequest().screeningId(), context.getBookingId());
        context.setSeatReserved(true);
    }

    @Override
    public void compensate(SagaContext context) {
        if (context.isReserveAttempted()) {
            movieClientPort.releaseSeat(context.getRequest().screeningId(), context.getBookingId());
            context.setSeatReserved(false);
        }
    }
}
