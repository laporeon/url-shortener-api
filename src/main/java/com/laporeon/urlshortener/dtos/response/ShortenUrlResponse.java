package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;

public record ShortenUrlResponse(
        String shortUrl,
        Instant expiresAt) {
}
