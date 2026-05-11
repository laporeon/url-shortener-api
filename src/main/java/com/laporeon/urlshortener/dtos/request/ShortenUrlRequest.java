package com.laporeon.urlshortener.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UrlRequestDTO(
        @Pattern(
                regexp = "https?://([\\w-]+\\.)+[\\w-]+(:\\d+)?(/\\S*)?",
                message = "Invalid URL format. Please provide a valid URL (e.g., https://example.com)."
        )
        @NotBlank(message = "URL is required.")
        String originalUrl,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Future(message = "Expiration date must be a future date (format: yyyy-MM-dd).")
        LocalDate expirationDate
) {
}
