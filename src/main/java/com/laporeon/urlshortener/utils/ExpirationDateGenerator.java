package com.laporeon.urlshortener.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ExpirationDateGenerator {

    public LocalDateTime generateExpiresAt(LocalDate expirationDate) {

        if (expirationDate != null) {
            return expirationDate.atTime(23, 0, 0);
        }

        return LocalDate.now().plusDays(1).atTime(23,0,0);
    }
}
