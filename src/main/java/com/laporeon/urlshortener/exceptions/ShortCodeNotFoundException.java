package com.laporeon.urlshortener.exceptions;

public class ShortCodeNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Short code %s does not exist or has expired.";

    public ShortCodeNotFoundException(String shortCode) {
        super(DEFAULT_MESSAGE.formatted(shortCode));
    }
}
