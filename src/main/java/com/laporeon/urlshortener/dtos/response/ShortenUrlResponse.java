package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;

public record UrlResponseDTO(
        String shortUrl,
        Instant expiresAt) {
}
