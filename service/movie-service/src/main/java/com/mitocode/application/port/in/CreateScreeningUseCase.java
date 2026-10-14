package com.mitocode.application.port.in;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.application.dto.ScreeningResponse;

public interface CreateScreeningUseCase {

    ScreeningResponse createScreening(CreateScreeningRequest request);
}
