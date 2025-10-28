package com.laporeon.urlshortener.dtos.response;

import java.time.LocalDateTime;

public record UrlResponseDTO(String shortUrl, LocalDateTime expiresAt) {
}
