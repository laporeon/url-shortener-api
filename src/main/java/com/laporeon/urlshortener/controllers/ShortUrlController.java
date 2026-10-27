package com.laporeon.urlshortener.controllers;

import com.laporeon.urlshortener.controllers.docs.RedirectDocumentation;
import com.laporeon.urlshortener.controllers.docs.ShortenUrlDocumentation;
import com.laporeon.urlshortener.dtos.request.ShortenUrlRequest;
import com.laporeon.urlshortener.dtos.response.ShortenUrlResponse;
import com.laporeon.urlshortener.services.ShortUrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Short URL")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @ShortenUrlDocumentation
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest dto, HttpServletRequest request) {
        ShortenUrlResponse shortenUrlResponse = shortUrlService.shortenUrl(dto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenUrlResponse);
    }

    @RedirectDocumentation
    @GetMapping("/{shortCode:[A-Za-z0-9]{7}}")
    public ResponseEntity<Void> redirect(@PathVariable("shortCode") String shortCode) {
        String originalUrl = shortUrlService.findByShortCode(shortCode).getOriginalUrl();

        log.info("Redirecting | shortCode={} | target={}", shortCode, originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();

    }
}
