package com.laporeon.urlshortener.dtos.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequestDTO(
        @URL(message = "Invalid or incorrectly formatted URL.")
        @NotBlank(message = "Original URL cannot be empty.")
        String originalUrl
) {
}
