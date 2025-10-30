package com.laporeon.urlshortener.services;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.mappers.UrlMapper;
import com.laporeon.urlshortener.repositories.UrlRepository;
import com.laporeon.urlshortener.utils.BaseUrlGenerator;
import com.laporeon.urlshortener.utils.ShortCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.laporeon.urlshortener.commom.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private ShortCodeGenerator codeGenerator;

    @Mock
    private BaseUrlGenerator baseUrlGenerator;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("Should return UrlResponseDTO when shortening Url with valid request data")
    void shortenUrl_ShouldReturnUrlResponseDTOWhenRequestDataIsValid() {
        when(codeGenerator.generateShortCode()).thenReturn(VALID_SHORT_CODE);
        when(baseUrlGenerator.generateBaseUrl(request)).thenReturn(BASE_URL);
        when(urlMapper.toEntity(any(UrlRequestDTO.class), eq(VALID_SHORT_CODE))).thenReturn(SAVED_URL_ENTITY);
        when(urlMapper.toDTO(any(Url.class), eq(BASE_URL))).thenReturn(URL_RESPONSE_DTO);
        when(urlRepository.existsByShortCode(VALID_SHORT_CODE)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(SAVED_URL_ENTITY);

        UrlResponseDTO sut = urlService.shortenUrl(VALID_URL_REQUEST_DTO);

        assertThat(sut.shortUrl()).isEqualTo(URL_RESPONSE_DTO.shortUrl());
        assertThat(sut.expiresAt()).isEqualTo(URL_RESPONSE_DTO.expiresAt());

        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRepository, atLeastOnce()).existsByShortCode(VALID_SHORT_CODE);
    }

    @Test
    @DisplayName("Should return Url entity when short code exists and has not expired")
    void findByShortCode_ShouldReturnUrlEntityWhenShortCodeExistsAndIsNotExpired() {
        when(urlRepository.findByShortCode(VALID_SHORT_CODE)).thenReturn(Optional.of(SAVED_URL_ENTITY));

        Url sut = urlService.findByShortCode(VALID_SHORT_CODE);

        assertThat(sut.getId()).isEqualTo(SAVED_URL_ENTITY.getId());
        assertThat(sut.getShortCode()).isEqualTo(SAVED_URL_ENTITY.getShortCode());
        assertThat(sut.getExpiresAt()).isEqualTo(SAVED_URL_ENTITY.getExpiresAt());

        verify(urlRepository, times(1)).findByShortCode(VALID_SHORT_CODE);
    }

    @Test
    @DisplayName("Should throw exception when short code does not exist or has expired")
    void findByShortCode_ShouldThrowExceptionWhenShortCodeNotFoundOrHasExpired() {
        when(urlRepository.findByShortCode(EXPIRED_SHORT_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.findByShortCode(EXPIRED_SHORT_CODE))
                .isInstanceOf(ShortCodeNotFoundException.class);

        verify(urlRepository, times(1)).findByShortCode(EXPIRED_SHORT_CODE);
    }
}
