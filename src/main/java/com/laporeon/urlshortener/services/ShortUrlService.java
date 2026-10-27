package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.ShortenUrlRequest;
import com.laporeon.urlshortener.dtos.response.ShortenUrlResponse;
import com.laporeon.urlshortener.entities.ShortUrl;
import com.laporeon.urlshortener.exceptions.custom.ShortCodeNotFoundException;
import com.laporeon.urlshortener.helpers.BaseUrlGenerator;
import com.laporeon.urlshortener.helpers.ExpirationDateGenerator;
import com.laporeon.urlshortener.helpers.ShortCodeGenerator;
import com.laporeon.urlshortener.repositories.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator codeGenerator;
    private final ExpirationDateGenerator dateGenerator;
    private final BaseUrlGenerator baseUrlGenerator;

    @Transactional
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest dto, HttpServletRequest request) {
        String shortCode;
        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        Instant expiresAt = dateGenerator.generateExpiresAt(dto.expirationDate());

        ShortUrl shortUrl = ShortUrl.create(dto.originalUrl(), shortCode, expiresAt);

        urlRepository.save(shortUrl);

        log.info("Short code '{}' generated for URL {} at {}", shortCode, shortUrl.getOriginalUrl(), Instant.now());

        String baseURL = baseUrlGenerator.generateBaseUrl(request);

        return new ShortenUrlResponse(
                baseURL + "/" + shortUrl.getShortCode(),
                shortUrl.getExpiresAt()
        );
    }

    public ShortUrl findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }
}
