package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ExpirationDateGenerator;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HttpServletRequest request;
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator codeGenerator;
    private final BaseUrlGenerator baseUrlGenerator;
    private final ExpirationDateGenerator dateGenerator;


    public UrlResponseDTO shortenUrl(UrlRequestDTO dto) {
        String shortCode;
        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        LocalDateTime expiresAt = dateGenerator.generateExpiresAt(dto.expirationDate());

        Url url = Url.builder()
                     .shortCode(shortCode)
                     .originalUrl(dto.originalUrl())
                     .expiresAt(expiresAt)
                     .build();

        urlRepository.save(url);

        log.info("Short code: {} generated for URL: {}", shortCode, url.getOriginalUrl());

        String baseURL = baseUrlGenerator.generateBaseUrl(request);
        String shortUrl = baseURL + "/" + url.getShortCode();

        return new UrlResponseDTO(
                shortUrl,
                url.getExpiresAt()
        );
    }

    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }
}
