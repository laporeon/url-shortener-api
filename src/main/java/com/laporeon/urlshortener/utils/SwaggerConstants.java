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
            "messages": [
                "Invalid URL format. Make sure to include http:// or https:// and a valid domain name (e.g https://www.google.com)"
            ],
            "timestamp": "2025-10-29T15:19:52.121160501Z"
            }
        """;

    public static final String NOT_FOUND_ERROR_MESSAGE = """
        {
            "status": 404,
            "error": "NOT_FOUND",
            "messages": [
                "Short code c3esjMb does not exist or has expired."
            ],
            "timestamp": "2025-10-29T15:19:52.121160501Z"
        }
        """;
}
