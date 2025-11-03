package com.laporeon.urlshortener.utils;

public class SwaggerConstants {

    public static final String URL_REQUEST_EXAMPLE = """
        {
            "originalUrl": "https://www.google.com/",
            "expirationDate": "2025-10-29"
        }
        """;

    public static final String URL_RESPONSE_EXAMPLE = """
        {
            "shortUrl": "https://localhost:8080/v2esjMb",
            "expiresAt": "2025-10-29T23:00:00Z"
        }
        """;

    public static final String VALIDATION_ERROR_MESSAGE = """
        {
            "status": 400,
            "error": "BAD_REQUEST",
            "messages": {
              "originalUrl": "Invalid URL format. Please provide a valid URL (e.g., https://example.com).",
              "expirationDate": "Expiration date must be a future date (format: yyyy-MM-dd)."
            },
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;

    public static final String NOT_FOUND_ERROR_MESSAGE = """
        {
            "status": 404,
            "error": "NOT_FOUND",
            "messages": {
                "shortCode": "Short code 845PGwV does not exist or has expired."
            },
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;
}
