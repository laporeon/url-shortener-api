package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.mappers.UrlMapper;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ShortCodeGenerator codeGenerator;
    private String shortCode;

    private LocalDateTime expirationDate = LocalDate.now().plusDays(1).atTime(23, 0);;

    public UrlResponseDTO shortenUrl(UrlRequestDTO dto) {
        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        Url url = urlRepository.save(urlMapper.toEntity(dto, shortCode, expirationDate));
        return urlMapper.toDTO(url);
    }
}
