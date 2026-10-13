package com.mitocode.application.port.in;

import com.mitocode.application.dto.ScreeningAvailabilityResponse;

import java.util.UUID;

public interface GetScreeningUseCase {

    ScreeningAvailabilityResponse getScreening(UUID screeningId);
}
