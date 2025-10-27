package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDTO(int status, String error, List<String> messages, Instant timestamp) {
}
