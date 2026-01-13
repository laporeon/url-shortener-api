package com.laporeon.urlshortener.utils;

public class SwaggerExamples {

    public static final String CREATE_SHORT_URL_REQUEST = """
        {
            "originalUrl": "https://www.google.com/",
            "expirationDate": "2025-10-29"
        }
        """;

    public static final String SHORTEN_URL_SUCCESS_RESPONSE = """
        {
            "shortUrl": "https://localhost:8080/v2esjMb",
            "expiresAt": "2025-11-10T14:27:34.238168440Z"
        }
        """;

    public static final String VALIDATION_ERROR_RESPONSE = """
        {
            "status": 400,
            "type": "VALIDATION_ERROR",
            "message": "Request validation failed for one or more fields",
            "details": {
              "originalUrl": "Invalid URL format. Please provide a valid URL (e.g., https://example.com).",
              "expirationDate": "Expiration date must be a future date (format: yyyy-MM-dd)."
            },
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;

    public static final String NOT_FOUND_ERROR_RESPONSE = """
        {
            "status": 404,
            "type": "NOT_FOUND_ERROR",
            "message": "Short code 845PGwV does not exist or has expired.",
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;

    public static final String INTERNAL_ERROR_RESPONSE = """
        {
            "status": 500,
            "type": "INTERNAL_SERVER_ERROR",
            "message": "An unexpected error occurred",
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;
}
