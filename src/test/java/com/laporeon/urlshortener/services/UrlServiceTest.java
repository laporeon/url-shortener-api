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
import static org.mockito.Mockito.*;

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
    private HttpServletRequest request;

    @InjectMocks
    private UrlService urlService;

    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String VALID_URL = "https://www.youtube.com";
    private static final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusDays(5);
    private static final String BASE_URL = "http://localhost:8080";

    private Instant expiresAt;
    private Url mockedUrlEntity;

    @BeforeEach
    void setUp() {
        expiresAt = Instant.now().plus(5, ChronoUnit.DAYS);

        mockedUrlEntity = Url.builder()
                       .id("67f81446994a2cef3456c9b5")
                       .shortCode(VALID_SHORT_CODE)
                       .originalUrl(VALID_URL)
                       .expiresAt(expiresAt)
                       .createdAt(Instant.now())
                       .build();
    }

    @Test
    @DisplayName("Should shorten Url successfully when given valid request data")
    void shouldShortenUrlSuccessfullyWhenGivenRequestData() {
        UrlRequestDTO requestDTO = new UrlRequestDTO(VALID_URL, VALID_EXPIRATION_DATE);

        when(dateGenerator.generateExpiresAt(eq(VALID_EXPIRATION_DATE))).thenReturn(expiresAt);
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(mockedUrlEntity);
        when(baseUrlGenerator.generateBaseUrl(any())).thenReturn(BASE_URL);

        UrlResponseDTO response = urlService.shortenUrl(requestDTO, request);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + VALID_SHORT_CODE);
        assertThat(response.expiresAt().isAfter(Instant.now()));

        verify(codeGenerator, times(1)).generateShortCode();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRepository, times(1)).existsByShortCode(VALID_SHORT_CODE);
    }

    @Test
    @DisplayName("Should regenerate short code when collision occurs")
    void shouldRegenerateShortCodeWhenCollisionOccurs() {
        UrlRequestDTO requestDTO = new UrlRequestDTO(VALID_URL, VALID_EXPIRATION_DATE);
        String newShortCode = "f1g2h3";

        Url savedUrlEntity = Url.builder()
                                .id("67f81446994a2cef3456c9b5")
                                .shortCode(newShortCode)  // ← short code correto
                                .originalUrl(VALID_URL)
                                .expiresAt(expiresAt)
                                .createdAt(Instant.now())
                                .build();

        when(dateGenerator.generateExpiresAt(eq(VALID_EXPIRATION_DATE))).thenReturn(expiresAt);
        when(codeGenerator.generateShortCode())
                .thenReturn(VALID_SHORT_CODE)
                .thenReturn(newShortCode);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(true);
        when(urlRepository.existsByShortCode(newShortCode)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrlEntity);
        when(baseUrlGenerator.generateBaseUrl(any())).thenReturn(BASE_URL);

        UrlResponseDTO response = urlService.shortenUrl(requestDTO, request);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + newShortCode);
        assertThat(response.expiresAt()).isAfter(Instant.now());

        verify(codeGenerator, times(2)).generateShortCode();
        verify(urlRepository, times(2)).existsByShortCode(any());
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should return URL when short code exists and has not expired")
    void shouldReturnUrlEntityWhenShortCodeExistsAndIsNotExpired() {
        when(urlRepository.findByShortCode(VALID_SHORT_CODE)).thenReturn(Optional.of(mockedUrlEntity));

        String result = urlService.findByShortCode(VALID_SHORT_CODE).getOriginalUrl();

        assertThat(result).isEqualTo(mockedUrlEntity.getOriginalUrl());

        verify(urlRepository, times(1)).findByShortCode(VALID_SHORT_CODE);
    }


    @Test
    @DisplayName("Should throw exception when short code does not exist or has expired")
    void shouldThrowExceptionWhenShortCodeNotFoundOrHasExpired() {
        String expiredShortCode = "expired";

        when(urlRepository.findByShortCode(expiredShortCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.findByShortCode(expiredShortCode))
                .isInstanceOf(ShortCodeNotFoundException.class);

        verify(urlRepository, times(1)).findByShortCode(expiredShortCode);
    }
}
