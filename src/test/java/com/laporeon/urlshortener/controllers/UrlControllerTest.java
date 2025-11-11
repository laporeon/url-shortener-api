package com.laporeon.urlshortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@DisplayName("UrlController Tests")
public class UrlControllerTest {

    private static final String VALID_ORIGINAL_URL = "https://www.youtube.com/";
    private static final String INVALID_ORIGINAL_URL = "invalidurl";
    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String INVALID_URL_MESSAGE = "Invalid URL format. Please provide a valid URL (e.g., https://example.com).";
    private static final String INVALID_EXPIRATION_DATE_MESSAGE = "Expiration date must be a future date (format: yyyy-MM-dd).";
    private static final String EXPIRED_SHORT_CODE_MESSAGE =  "Short code %s does not exist or has expired.";
    private static final String SHORTEN_URL_ENDPOINT = "/shorten-url";
    private static final String BASE_URL = "https://localhost:8080";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    private LocalDate validExpirationDate;
    private LocalDate invalidExpirationDate;
    private Instant createdAt;
    private Instant expiresAt;
    private Url mockedUrl;

    @BeforeEach
    void setUp() {
        validExpirationDate = LocalDate.now().plusDays(5);
        invalidExpirationDate = LocalDate.now().minusDays(5);
        createdAt = Instant.now();
        expiresAt = Instant.now().plus(5, ChronoUnit.DAYS);

        mockedUrl = Url.builder()
                       .id(new ObjectId().toString())
                       .shortCode(VALID_SHORT_CODE)
                       .originalUrl(VALID_ORIGINAL_URL)
                       .expiresAt(expiresAt)
                       .createdAt(createdAt)
                       .build();
    }

    @Test
    @DisplayName("POST /shorten-url - Should return 201 when given valid request data")
     void shouldReturnCreatedWhenGivenValidRequestData() throws Exception {
        UrlRequestDTO validRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, validExpirationDate);

        UrlResponseDTO urlResponseDTO = new UrlResponseDTO(
                BASE_URL + "/" + VALID_SHORT_CODE,
                expiresAt
        );

        String expectedShortUrl = BASE_URL + "/" + VALID_SHORT_CODE;

        when(urlService.shortenUrl(any(UrlRequestDTO.class), any(HttpServletRequest.class))).thenReturn(urlResponseDTO);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(expectedShortUrl))
               .andExpect(jsonPath("$.expiresAt").value(expiresAt.toString()));
    }

    @Test
    @DisplayName("POST /shorten-url - Should return 400 when given invalid URL")
    void shouldReturn400WhenGivenInvalidUrl() throws Exception {
        UrlRequestDTO invalidRequest = new UrlRequestDTO(INVALID_ORIGINAL_URL, validExpirationDate);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.messages").isMap())
               .andExpect(jsonPath("$.messages.originalUrl").value(INVALID_URL_MESSAGE));
    }

    @Test
    @DisplayName("POST /shorten-url - Should return 400 when given invalid expiration date")
    void shouldReturn400WhenGivenInvalidExpirationDate() throws Exception {
        UrlRequestDTO invalidRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, invalidExpirationDate);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.messages").isMap())
               .andExpect(jsonPath("$.messages.expirationDate").value(INVALID_EXPIRATION_DATE_MESSAGE));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 301 when given existing short code")
    void shouldReturn301WhenGivenExistingShortCode() throws Exception {
        when(urlService.findByShortCode(VALID_SHORT_CODE)).thenReturn(mockedUrl);

        mockMvc.perform(get("/" + VALID_SHORT_CODE))
               .andExpect(status().isMovedPermanently())
               .andExpect(header().string("Location", mockedUrl.getOriginalUrl()));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 404 when given expired short code")
    void shouldReturn404WhenGivenExpiredShortCode() throws Exception {
        String expiredShortCode = "expired";

        when(urlService.findByShortCode(expiredShortCode))
                .thenThrow(new ShortCodeNotFoundException(expiredShortCode));

        mockMvc.perform(get("/" + expiredShortCode))
               .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages").isMap())
                .andExpect(jsonPath("$.messages.shortCode")
                                   .value(EXPIRED_SHORT_CODE_MESSAGE.formatted(expiredShortCode)));
    }
}
