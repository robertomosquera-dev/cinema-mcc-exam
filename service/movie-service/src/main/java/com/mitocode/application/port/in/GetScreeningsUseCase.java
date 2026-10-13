package com.mitocode.application.port.in;

import com.mitocode.application.dto.ScreeningResponse;

import java.util.List;

public interface GetScreeningsUseCase {

    List<ScreeningResponse> getScreenings();
}
