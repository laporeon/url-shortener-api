package com.laporeon.urlshortener.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(int status,
                               String type,
                               String message,
                               Map<String, String> details,
                               Instant timestamp) {
}
