package com.mitocode.infrastructure.utils;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

public final class ResourceUriUtils {

    private ResourceUriUtils() {
    }

    public static URI created(UriComponentsBuilder uriBuilder, String resourcePath, UUID id) {
        return uriBuilder.path(resourcePath).buildAndExpand(id).toUri();
    }
}
