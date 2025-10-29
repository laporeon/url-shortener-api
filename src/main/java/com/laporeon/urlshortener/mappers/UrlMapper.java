package com.laporeon.urlshortener.mappers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.utils.ExpirationDateGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UrlMapper {

    private final ExpirationDateGenerator dateGenerator;

    public UrlResponseDTO toDTO(Url url, String baseURL) {
        String shortUrl = baseURL + "/" + url.getShortCode();

        return new UrlResponseDTO(shortUrl, url.getExpiresAt());
    }

    public Url toEntity(UrlRequestDTO dto, String shortCode) {
        LocalDateTime expiresAt = dateGenerator.generateExpiresAt(dto.expirationDate());

        Url url = new Url();
        url.setOriginalUrl(dto.originalUrl());
        url.setShortCode(shortCode);
        url.setExpiresAt(expiresAt);
        return url;
    }

}
