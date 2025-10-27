package com.laporeon.urlshortener.dtos.response;

import java.time.LocalDateTime;

public record UrlResponseDTO(String shortCode, LocalDateTime expiresAt) {
}
