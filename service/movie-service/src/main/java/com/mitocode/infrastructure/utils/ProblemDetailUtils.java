package com.mitocode.infrastructure.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public final class ProblemDetailUtils {

    private ProblemDetailUtils() {
    }

    public static ProblemDetail create(HttpStatus status, String title, String detail, URI instance) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(title);
        problem.setInstance(instance);
        return problem;
    }
}
