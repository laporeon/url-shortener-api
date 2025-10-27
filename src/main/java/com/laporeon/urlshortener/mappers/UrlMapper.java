package com.laporeon.urlshortener.mappers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UrlMapper {

    private final ShortCodeGenerator codeGenerator;
    private final UrlRepository urlRepository;

    public UrlResponseDTO toDTO(Url url) {
        return new UrlResponseDTO(
                url.getShortCode(),
                url.getExpiresAt()
        );
    }

    public Url toEntity(UrlRequestDTO dto, String shortCode, LocalDateTime expirationDate) {
        Url url = new Url();
        url.setOriginalUrl(dto.originalUrl());
        url.setShortCode(shortCode);
        url.setExpiresAt(expirationDate);
        return url;
    }

}
