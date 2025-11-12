package com.laporeon.urlshortener.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ExpirationDateGenerator {

    public Instant generateExpiresAt(LocalDate expirationDate) {

        if (expirationDate != null) {
            long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
            return Instant.now().plus(daysUntilExpiration, ChronoUnit.DAYS);
        }

        return Instant.now().plus(24, ChronoUnit.HOURS);
    }
}
