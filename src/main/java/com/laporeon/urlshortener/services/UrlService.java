package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ExpirationDateGenerator;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${app.base-url}")
    private String BASE_URL;

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator codeGenerator;
    private final ExpirationDateGenerator dateGenerator;


    public UrlResponseDTO shortenUrl(UrlRequestDTO dto) {
        String shortCode;
        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        Instant expiresAt = dateGenerator.generateExpiresAt(dto.expirationDate());

        Url url = Url.builder()
                     .shortCode(shortCode)
                     .originalUrl(dto.originalUrl())
                     .expiresAt(expiresAt)
                     .build();

        urlRepository.save(url);

        log.info("Short code: {} generated for URL: {}", shortCode, url.getOriginalUrl());

        String shortUrl = BASE_URL + "/" + url.getShortCode();

        return new UrlResponseDTO(
                shortUrl,
                url.getExpiresAt()
        );
    }

    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }
}
