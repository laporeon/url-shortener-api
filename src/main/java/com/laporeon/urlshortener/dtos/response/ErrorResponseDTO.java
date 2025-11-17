package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponseDTO(int status,
                               String type,
                               String title,
                               Map<String, String> details,
                               Instant timestamp) {
}
