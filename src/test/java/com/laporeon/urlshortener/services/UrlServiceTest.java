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
import org.bson.types.ObjectId;
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

    private Instant createdAt;
    private Instant expiresAt;
    private Url urlEntity;

    @BeforeEach
    void setUp() {
        createdAt = Instant.now();
        expiresAt = Instant.now().plus(5, ChronoUnit.DAYS);

        urlEntity = Url.builder()
                       .id(new ObjectId().toString())
                       .shortCode(VALID_SHORT_CODE)
                       .originalUrl(VALID_URL)
                       .expiresAt(expiresAt)
                       .createdAt(createdAt)
                       .build();
    }

    @Test
    @DisplayName("Should shorten Url successfully when given valid request data")
    void shouldShortenUrlSuccessfullyWhenGivenRequestData() {
        UrlRequestDTO requestDTO = new UrlRequestDTO(VALID_URL, VALID_EXPIRATION_DATE);

        when(dateGenerator.generateExpiresAt(eq(VALID_EXPIRATION_DATE))).thenReturn(expiresAt);
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);
        when(baseUrlGenerator.generateBaseUrl(request)).thenReturn(BASE_URL);

        UrlResponseDTO response = urlService.shortenUrl(requestDTO, request);

        assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + VALID_SHORT_CODE);
        assertThat(response.expiresAt()).isEqualTo(expiresAt);

        verify(codeGenerator, times(1)).generateShortCode();
        verify(baseUrlGenerator, times(1)).generateBaseUrl(request);
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRepository, times(1)).existsByShortCode(VALID_SHORT_CODE);
    }

    @Test
    @DisplayName("Should return URL when short code exists and has not expired")
    void shouldReturnUrlEntityWhenShortCodeExistsAndIsNotExpired() {
        when(urlRepository.findByShortCode(VALID_SHORT_CODE)).thenReturn(Optional.of(urlEntity));

        Url result = urlService.findByShortCode(VALID_SHORT_CODE);

        assertThat(result.getId()).isEqualTo(urlEntity.getId());
        assertThat(result.getShortCode()).isEqualTo(urlEntity.getShortCode());
        assertThat(result.getExpiresAt()).isEqualTo(urlEntity.getExpiresAt());

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
