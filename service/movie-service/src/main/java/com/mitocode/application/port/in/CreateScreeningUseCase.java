package com.mitocode.application.port.in;

import com.mitocode.application.dto.CreateScreeningRequest;
import com.mitocode.domain.model.Screening;

public interface CreateScreeningUseCase {

    Screening createScreening(CreateScreeningRequest request);
}
