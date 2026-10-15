package com.mitocode.application.saga.step;

import com.mitocode.application.saga.IStep;
import com.mitocode.application.saga.SagaContext;
import com.mitocode.application.port.out.BookingRepositoryPort;
import com.mitocode.domain.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreatePendingBookingStep implements IStep {
    private final BookingRepositoryPort repository;

    @Override
    public void execute(SagaContext context) {
        var request = context.getRequest();
        Booking pending = Booking.pending(request.screeningId(),
                context.getScreeningAvailabilityResponse().movieTitle(), request.customerId(), request.totalAmount());
        context.setPendingCandidateId(pending.getId());
        Booking saved = repository.save(pending);
        context.setBooking(saved);
        context.setBookingId(saved.getId());
    }

    @Override
    public void compensate(SagaContext context) {
        UUID id = context.getBookingId() != null ? context.getBookingId() : context.getPendingCandidateId();
        if (id != null) {
            var existing = repository.findById(id);
            if (existing.isEmpty()) return;
            Booking booking = existing.get();
            booking.cancel();
            context.setBooking(repository.save(booking));
        }
    }
}
