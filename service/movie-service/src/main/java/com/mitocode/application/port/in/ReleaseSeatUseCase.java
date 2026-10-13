package com.mitocode.application.port.in;

import com.mitocode.application.dto.ReleaseSeatResponse;

import java.util.UUID;

public interface ReleaseSeatUseCase {

    ReleaseSeatResponse releaseSeat(UUID screeningId);
}
