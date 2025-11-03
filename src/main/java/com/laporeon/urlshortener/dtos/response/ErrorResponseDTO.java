package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponseDTO(int status,
                               String error,
                               Map<String, String> messages,
                               Instant timestamp) {
}
