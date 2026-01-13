package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import com.laporeon.urlshortener.utils.ExpirationDateGenerator;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
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
    public UrlResponseDTO shortenUrl(UrlRequestDTO dto, HttpServletRequest request) {
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

        log.info("Short code '{}' generated for URL {} at {}", shortCode, url.getOriginalUrl(), Instant.now());

        String baseURL = baseUrlGenerator.generateBaseUrl(request);

        return new UrlResponseDTO(
                baseURL + "/" + url.getShortCode(),
                url.getExpiresAt()
        );
    }

    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }
}
