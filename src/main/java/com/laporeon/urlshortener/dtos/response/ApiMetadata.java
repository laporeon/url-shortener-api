package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;
import java.util.Map;

public record ApiMetadataDTO(
        String name,
        String version,
        String status,
        Instant timestamp,
        Map<String, String> endpoints,
        String documentation) {
}
