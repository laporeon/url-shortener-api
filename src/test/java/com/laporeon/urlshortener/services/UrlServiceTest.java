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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UrlService Tests")
public class UrlServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator codeGenerator;

    @Mock
    private ExpirationDateGenerator dateGenerator;

    @Mock
    private BaseUrlGenerator baseUrlGenerator;

    @InjectMocks
    private UrlService urlService;

    private Url validUrlEntity;
    private UrlRequestDTO validRequestDTO;
    private UrlResponseDTO expectedResponseDTO;

    private final String VALID_SHORT_CODE = "a1b2c3d";
    private final String VALID_URL = "https://www.youtube.com";
    private final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusDays(1);
    private final String BASE_URL = "http://localhost:8080/";

    @BeforeEach
    void setUp() {
        validRequestDTO = new UrlRequestDTO(
                VALID_URL,
                VALID_EXPIRATION_DATE
        );
        validUrlEntity = Url.builder()
                            .id("69026cdf278513d576c67059")
                            .shortCode(VALID_SHORT_CODE)
                            .originalUrl(validRequestDTO.originalUrl())
                            .expiresAt(VALID_EXPIRATION_DATE.atTime(23,0,0))
                            .createdAt(LocalDateTime.now())
                            .build();
        expectedResponseDTO = new UrlResponseDTO(BASE_URL + "/" + VALID_SHORT_CODE, validUrlEntity.getExpiresAt());
    }

    @Test
    @DisplayName("Should shorten Url successfully when given valid request data")
    void shouldShourtenUrlSuccessfullyWhenGivenRequestData() {
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(baseUrlGenerator.generateBaseUrl(request)).thenReturn(BASE_URL);
        when(dateGenerator.generateExpiresAt(eq(validRequestDTO.expirationDate()))).thenReturn(validUrlEntity.getExpiresAt());
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(validUrlEntity);

        UrlResponseDTO sut = urlService.shortenUrl(validRequestDTO);

        assertThat(sut.shortUrl()).isEqualTo(expectedResponseDTO.shortUrl());
        assertThat(sut.expiresAt()).isEqualTo(expectedResponseDTO.expiresAt());

        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRepository, atLeastOnce()).existsByShortCode(VALID_SHORT_CODE);
    }

    @Test
    @DisplayName("Should return Url entity when short code exists and has not expired")
    void shouldReturnUrlEntityWhenShortCodeExistsAndIsNotExpired() {
        when(urlRepository.findByShortCode(VALID_SHORT_CODE)).thenReturn(Optional.of(validUrlEntity));

        Url sut = urlService.findByShortCode(VALID_SHORT_CODE);

        assertThat(sut.getId()).isEqualTo(validUrlEntity.getId());
        assertThat(sut.getShortCode()).isEqualTo(validUrlEntity.getShortCode());
        assertThat(sut.getExpiresAt()).isEqualTo(validUrlEntity.getExpiresAt());

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
