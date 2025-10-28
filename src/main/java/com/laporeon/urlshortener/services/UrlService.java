package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.mappers.UrlMapper;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ShortCodeGenerator codeGenerator;
    private final HttpServletRequest request;
    private final BaseUrlGenerator baseUrlGenerator;

    public UrlResponseDTO shortenUrl(UrlRequestDTO dto) {
        String shortCode;
        LocalDateTime expirationDate = LocalDate.now().plusDays(1).atTime(23, 0);;

        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        String baseURL = baseUrlGenerator.generateBaseUrl(request);

        Url url = urlRepository.save(urlMapper.toEntity(dto, shortCode, expirationDate));

        log.info("Saved URL: {}", url);

        return urlMapper.toDTO(url, baseURL);
    }
}
