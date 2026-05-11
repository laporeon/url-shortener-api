package com.laporeon.urlshortener.dtos.response;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String type,
        String message,
        List<FieldError> errors,
        Instant timestamp
) {

    public record FieldError(String field, String message) {}

}
