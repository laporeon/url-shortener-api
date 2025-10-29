package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.mappers.UrlMapper;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HttpServletRequest request;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ShortCodeGenerator codeGenerator;
    private final BaseUrlGenerator baseUrlGenerator;


    public UrlResponseDTO shortenUrl(UrlRequestDTO dto) {
        String shortCode;
        do {
            shortCode = codeGenerator.generateShortCode();
        } while(urlRepository.existsByShortCode(shortCode));

        String baseURL = baseUrlGenerator.generateBaseUrl(request);

        Url url = urlRepository.save(urlMapper.toEntity(dto, shortCode));

        log.info("Saved URL: {}", url);

        return urlMapper.toDTO(url, baseURL);
    }

    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new ShortCodeNotFoundException(shortCode));
    }
}
