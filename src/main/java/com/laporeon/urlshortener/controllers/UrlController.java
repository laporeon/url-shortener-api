package com.laporeon.urlshortener.controllers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.services.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten-url")
    public ResponseEntity<UrlResponseDTO> generateShortUrl(@Valid @RequestBody UrlRequestDTO dto) {
        UrlResponseDTO urlResponseDTO = urlService.shortenUrl(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponseDTO);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable("shortCode") String shortCode) {
        log.info("Short Code requested: {}", shortCode);

        Url url = urlService.findByShortCode(shortCode);

        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(URI.create(url.getOriginalUrl()));

        log.info("Redirecting to URL: {}", url.getOriginalUrl());

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();

    }
}
