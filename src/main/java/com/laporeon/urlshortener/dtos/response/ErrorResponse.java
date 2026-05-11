package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;

public record ErrorResponseDTO(int status,
                               String type,
                               String message,
                               Instant timestamp) {
}
