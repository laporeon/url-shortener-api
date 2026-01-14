package com.laporeon.urlshortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.ApiMetadataDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@DisplayName("UrlController Tests")
public class UrlControllerTest {

    private static final String VALID_ORIGINAL_URL = "https://www.youtube.com/";
    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String INVALID_URL_MESSAGE = "Invalid URL format. Please provide a valid URL (e.g., https://example.com).";
    private static final String INVALID_EXPIRATION_DATE_MESSAGE = "Expiration date must be a future date (format: yyyy-MM-dd).";
    private static final String VALIDATION_ERROR_TYPE =  "VALIDATION_ERROR";
    private static final String VALIDATION_ERROR_MESSAGE =  "Request validation failed for one or more fields";
    private static final String NOT_FOUND_ERROR_TYPE =  "NOT_FOUND_ERROR";
    private static final String NOT_FOUND_ERROR_MESSAGE =  "Short code %s does not exist or has expired.";
    private static final String SHORTEN_URL_ENDPOINT = "/shorten";
    private static final String BASE_URL = "https://localhost:8080";

    private static final String API_NAME = "URL Shortener API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_STATUS = "operational";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    private Url mockedUrlEntity;

    private UrlResponseDTO mockedUrlResponseDTO;

    @BeforeEach
    void setUp() {
        mockedUrlEntity = Url.builder()
                       .id("67f81446994a2cef3456c9b5")
                       .shortCode(VALID_SHORT_CODE)
                       .originalUrl(VALID_ORIGINAL_URL)
                       .expiresAt(Instant.now().plus(5, ChronoUnit.DAYS))
                       .createdAt(Instant.now())
                       .build();

        mockedUrlResponseDTO = new UrlResponseDTO(
                BASE_URL + "/" + VALID_SHORT_CODE,
                mockedUrlEntity.getExpiresAt()
        );

    }

    @Test
    @DisplayName("POST /shorten - Should return 201 when given valid request data")
     void shouldReturnCreatedWhenGivenValidRequestData() throws Exception {
        LocalDate validExpirationDate = LocalDate.now().plusDays(5);

        UrlRequestDTO validRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, validExpirationDate);

        when(urlService.shortenUrl(any(UrlRequestDTO.class), any(HttpServletRequest.class))).thenReturn(mockedUrlResponseDTO);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(mockedUrlResponseDTO.shortUrl()))
               .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("POST /shorten - Should return 400 when given invalid URL format")
    void shouldReturn400WhenGivenInvalidUrlFormat() throws Exception {
        String invalidOriginalUrl = "invalidurl";
        LocalDate validExpirationDate = LocalDate.now().plusDays(5);

        UrlRequestDTO invalidRequest = new UrlRequestDTO(invalidOriginalUrl, validExpirationDate);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.type").value(VALIDATION_ERROR_TYPE))
               .andExpect(jsonPath("$.message").value(VALIDATION_ERROR_MESSAGE))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].message").value(INVALID_URL_MESSAGE));
    }

    @Test
    @DisplayName("POST /shorten - Should return 400 when given invalid expiration date")
    void shouldReturn400WhenGivenInvalidExpirationDate() throws Exception {
        LocalDate invalidExpirationDate = LocalDate.now().minusDays(5);
        UrlRequestDTO invalidRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, invalidExpirationDate);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.type").value(VALIDATION_ERROR_TYPE))
               .andExpect(jsonPath("$.message").value(VALIDATION_ERROR_MESSAGE))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].message").value(INVALID_EXPIRATION_DATE_MESSAGE));
    }


    @Test
    @DisplayName("GET /{shortCode} - Should return 302 when given existing short code")
    void shouldReturn302WhenGivenExistingShortCode() throws Exception {
        when(urlService.findByShortCode(VALID_SHORT_CODE)).thenReturn(mockedUrlEntity);

        mockMvc.perform(get("/" + VALID_SHORT_CODE))
               .andExpect(status().isFound())
               .andExpect(header().string("Location", mockedUrlEntity.getOriginalUrl()));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 404 when given expired or non existent short code")
    void shouldReturn404WhenGivenExpiredOrNonExistingShortCode() throws Exception {
        String expiredShortCode = "expired";

        when(urlService.findByShortCode(expiredShortCode))
                .thenThrow(new ShortCodeNotFoundException(expiredShortCode));

        mockMvc.perform(get("/" + expiredShortCode))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.type").value(NOT_FOUND_ERROR_TYPE))
               .andExpect(jsonPath("$.message").value(NOT_FOUND_ERROR_MESSAGE.formatted(expiredShortCode)));
    }

    @Test
    @DisplayName("GET / - Should return API metadata information")
    void shouldReturnApiMetadataInformation() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value(API_NAME))
               .andExpect(jsonPath("$.version").value(API_VERSION))
               .andExpect(jsonPath("$.status").value(API_STATUS))
               .andExpect(jsonPath("$.timestamp").exists())
               .andExpect(jsonPath("$.endpoints").isMap())
               .andExpect(jsonPath("$.endpoints").value(hasValue("POST " + SHORTEN_URL_ENDPOINT)))
               .andExpect(jsonPath("$.documentation").exists());
    }
}
