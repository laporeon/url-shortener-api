package com.laporeon.urlshortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.services.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@DisplayName("UrlController Tests")
public class UrlControllerTest {

    private static final String BASE_URL = "https://localhost:8080";
    private static final String VALID_ORIGINAL_URL = "https://www.youtube.com/";
    private static final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusDays(5);
    private static final String INVALID_ORIGINAL_URL = "invalidurl";
    private static final LocalDate INVALID_EXPIRATION_DATE = LocalDate.now().minusDays(3);
    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String EXPIRED_SHORT_CODE = "e1x2p3r";
    private static final String INVALID_URL_MESSAGE = "Invalid URL format. Please provide a valid URL (e.g., https://example.com).";
    private static final String INVALID_EXPIRATION_DATE_MESSAGE = "Expiration date must be a future date (format: yyyy-MM-dd).";
    private static final String EXPIRED_SHORT_CODE_MESSAGE = "Short code " + EXPIRED_SHORT_CODE + " does not exist or has expired.";
    private static final String SHORTEN_URL_ENDPOINT = "/shorten-url";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("POST /shorten-url - Should return 201 when given valid request data")
     void shouldReturnCreatedWhenGivenValidRequestData() throws Exception {
        UrlRequestDTO validRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, VALID_EXPIRATION_DATE);

        String expectedShortUrl = BASE_URL + "/" + VALID_SHORT_CODE;
        String expectedExpiresAt = VALID_EXPIRATION_DATE.atTime(23, 0, 0)
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        UrlResponseDTO urlResponseDTO = new UrlResponseDTO(
                BASE_URL + "/" + VALID_SHORT_CODE,
                VALID_EXPIRATION_DATE.atTime(23,0,0)
        );

        when(urlService.shortenUrl(any(UrlRequestDTO.class))).thenReturn(urlResponseDTO);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(expectedShortUrl))
               .andExpect(jsonPath("$.expiresAt").value(expectedExpiresAt));
    }

    @Test
    @DisplayName("POST /shorten-url - Should return 400 when given invalid URL")
    void shouldReturn400WhenGivenInvalidUrl() throws Exception {
        UrlRequestDTO invalidRequest = new UrlRequestDTO(INVALID_ORIGINAL_URL, VALID_EXPIRATION_DATE);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.messages").isArray())
               .andExpect(jsonPath("$.messages", hasItem(INVALID_URL_MESSAGE)));
    }

    @Test
    @DisplayName("POST /shorten-url - Should return 400 when given invalid expiration date")
    void shouldReturn400WhenGivenInvalidExpirationDate() throws Exception {
        UrlRequestDTO invalidRequest = new UrlRequestDTO(VALID_ORIGINAL_URL, INVALID_EXPIRATION_DATE);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(jsonPath("$.messages").isArray())
               .andExpect(jsonPath("$.messages", hasItem(INVALID_EXPIRATION_DATE_MESSAGE)));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 301 when given existing short code")
    void shouldReturn301WhenGivenExistingShortCode() throws Exception {
        Url mockedUrl = Url.builder()
                         .shortCode(VALID_SHORT_CODE)
                         .originalUrl(VALID_ORIGINAL_URL)
                         .expiresAt(VALID_EXPIRATION_DATE.atTime(23, 0, 0))
                         .build();

        when(urlService.findByShortCode(VALID_SHORT_CODE)).thenReturn(mockedUrl);

        mockMvc.perform(get("/" + VALID_SHORT_CODE))
               .andExpect(status().isMovedPermanently())
               .andExpect(header().string("Location", mockedUrl.getOriginalUrl()));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 400 when given expired short code")
    void shouldReturn301WhenGivenExpiredShortCode() throws Exception {
        when(urlService.findByShortCode(EXPIRED_SHORT_CODE))
                .thenThrow(new ShortCodeNotFoundException(EXPIRED_SHORT_CODE));

        mockMvc.perform(get("/" + EXPIRED_SHORT_CODE))
               .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasItem(EXPIRED_SHORT_CODE_MESSAGE)));
    }
}
