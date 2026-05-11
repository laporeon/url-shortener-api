package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.ShortenUrlRequest;
import com.laporeon.urlshortener.dtos.response.ShortenUrlResponse;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.custom.ShortCodeNotFoundException;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import com.laporeon.urlshortener.utils.ExpirationDateGenerator;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UrlService Tests")
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator codeGenerator;

    @Mock
    private ExpirationDateGenerator dateGenerator;

    @Mock
    private BaseUrlGenerator baseUrlGenerator;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private UrlService urlService;

    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String VALID_URL = "https://www.youtube.com";
    private static final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusDays(5);
    private static final Instant VALID_EXPIRES_AT = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final Instant DEFAULT_EXPIRES_AT = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final String BASE_URL = "http://localhost:8080";

    private Url mockedUrlEntity;

    @BeforeEach
    void setUp() {
        mockedUrlEntity = Url.create(BASE_URL, VALID_SHORT_CODE, VALID_EXPIRES_AT);
    }

    @Test
    @DisplayName("Should create shortened URL with generated code and expiration")
    void shouldCreateShortenedUrlWithGeneratedCodeAndExpiration() {
        ShortenUrlRequest requestDTO = new ShortenUrlRequest(VALID_URL, VALID_EXPIRATION_DATE);

        when(dateGenerator.generateExpiresAt(eq(VALID_EXPIRATION_DATE))).thenReturn(VALID_EXPIRES_AT);
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(mockedUrlEntity);
        when(baseUrlGenerator.generateBaseUrl(any())).thenReturn(BASE_URL);

        ShortenUrlResponse response = urlService.shortenUrl(requestDTO, httpRequest);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + VALID_SHORT_CODE);
        assertThat(response.expiresAt().isAfter(Instant.now()));

        verify(dateGenerator, times(1)).generateExpiresAt(eq(VALID_EXPIRATION_DATE));
        verify(codeGenerator, times(1)).generateShortCode();
        verify(baseUrlGenerator, times(1)).generateBaseUrl(httpRequest);
        verify(urlRepository, times(1)).existsByShortCode(VALID_SHORT_CODE);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should use default expiration when date is null")
    void shouldUseDefaultExpirationWhenDateIsNull() {
        ShortenUrlRequest requestDTO = new ShortenUrlRequest(VALID_URL, null);

        Url urlWithDefaultExpiration = Url.create(BASE_URL, VALID_SHORT_CODE, DEFAULT_EXPIRES_AT);

        when(dateGenerator.generateExpiresAt(eq(null))).thenReturn(DEFAULT_EXPIRES_AT);
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(urlWithDefaultExpiration);
        when(baseUrlGenerator.generateBaseUrl(any())).thenReturn(BASE_URL);

        ShortenUrlResponse response = urlService.shortenUrl(requestDTO, httpRequest);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + VALID_SHORT_CODE);
        assertThat(response.expiresAt()).isEqualTo(DEFAULT_EXPIRES_AT);

        verify(dateGenerator, times(1)).generateExpiresAt(null);
        verify(codeGenerator, times(1)).generateShortCode();
        verify(baseUrlGenerator, times(1)).generateBaseUrl(httpRequest);
        verify(urlRepository, times(1)).existsByShortCode(VALID_SHORT_CODE);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should generate new code when duplicate detected")
    void shouldGenerateNewCodeWhenDuplicateDetected() {
        ShortenUrlRequest requestDTO = new ShortenUrlRequest(VALID_URL, VALID_EXPIRATION_DATE);
        String newValidShortCode = "f1g2h3";

        Url savedUrlEntity =  Url.create(BASE_URL, newValidShortCode, DEFAULT_EXPIRES_AT);

        when(dateGenerator.generateExpiresAt(eq(VALID_EXPIRATION_DATE))).thenReturn(VALID_EXPIRES_AT);
        when(codeGenerator.generateShortCode())
                .thenReturn(VALID_SHORT_CODE)
                .thenReturn(newValidShortCode);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(true);
        when(urlRepository.existsByShortCode(newValidShortCode)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrlEntity);
        when(baseUrlGenerator.generateBaseUrl(any())).thenReturn(BASE_URL);

        ShortenUrlResponse response = urlService.shortenUrl(requestDTO, httpRequest);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + newValidShortCode);
        assertThat(response.expiresAt()).isAfter(Instant.now());

        verify(dateGenerator, times(1)).generateExpiresAt(eq(VALID_EXPIRATION_DATE));
        verify(codeGenerator, times(2)).generateShortCode();
        verify(baseUrlGenerator, times(1)).generateBaseUrl(httpRequest);
        verify(urlRepository, times(2)).existsByShortCode(any());
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should retrieve original URL for valid short code")
    void shouldRetrieveOriginalUrlForValidShortCode() {
        when(urlRepository.findByShortCode(VALID_SHORT_CODE)).thenReturn(Optional.of(mockedUrlEntity));

        String result = urlService.findByShortCode(VALID_SHORT_CODE).getOriginalUrl();

        assertThat(result).isEqualTo(mockedUrlEntity.getOriginalUrl());

        verify(urlRepository, times(1)).findByShortCode(VALID_SHORT_CODE);
    }


    @Test
    @DisplayName("Should throw exception for invalid short code")
    void shouldThrowExceptionForInvalidShortCode() {
        String invalidShortCode = "invalid";

        when(urlRepository.findByShortCode(invalidShortCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.findByShortCode(invalidShortCode))
                .isInstanceOf(ShortCodeNotFoundException.class);

        verify(urlRepository, times(1)).findByShortCode(invalidShortCode);
    }
}
