package com.laporeon.urlshortener.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ExpirationDateGenerator {

    public LocalDateTime generateExpiresAt(LocalDate expirationDate) {

        LocalDateTime expiresAt;

        if (expirationDate == null) {
            expiresAt = LocalDate.now().plusDays(1).atTime(23,0,0);
        } else {
            expiresAt = expirationDate.atTime(23, 0, 0);
        }

        return expiresAt;
    }
}
