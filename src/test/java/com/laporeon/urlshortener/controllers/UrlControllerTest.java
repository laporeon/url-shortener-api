package com.laporeon.urlshortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.urlshortener.dtos.request.ShortenUrlRequest;
import com.laporeon.urlshortener.dtos.response.ShortenUrlResponse;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.exceptions.ShortCodeNotFoundException;
import com.laporeon.urlshortener.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@DisplayName("UrlController Tests")
public class UrlControllerTest {

    private static final String VALID_SHORT_CODE = "a1b2c3d";
    private static final String VALID_ORIGINAL_URL = "https://www.youtube.com/";
    private static final Instant VALID_EXPIRES_AT = Instant.now().plus(5, ChronoUnit.DAYS);
    private static final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusDays(5);
    private static final String BASE_URL = "http://localhost:8080/";
    private static final String SHORTEN_URL_ENDPOINT = "/shorten";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    private ShortenUrlResponse mockedShortenUrlResponse;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        mockedShortenUrlResponse = new ShortenUrlResponse(BASE_URL + VALID_SHORT_CODE, VALID_EXPIRES_AT);
    }

    @Test
    @DisplayName("POST /shorten - Should return 201 when given valid request data")
     void shouldReturnCreatedWhenGivenValidRequestData() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest(VALID_ORIGINAL_URL, VALID_EXPIRATION_DATE);

        when(urlService.shortenUrl(any(ShortenUrlRequest.class), any(HttpServletRequest.class))).thenReturn(mockedShortenUrlResponse);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(mockedShortenUrlResponse.shortUrl()))
               .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("POST /shorten - Should return 400 when given invalid URL format")
    void shouldReturn400WhenGivenInvalidUrlFormat() throws Exception {
        String invalidUrlMessage = "Invalid URL format. Please provide a valid URL (e.g., https://example.com).";
        ShortenUrlRequest invalidRequest = new ShortenUrlRequest("invalid-url", VALID_EXPIRATION_DATE);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value("Request validation failed for one or more fields"))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].message").value(invalidUrlMessage));
    }

    @Test
    @DisplayName("POST /shorten - Should return 400 when given invalid expiration date")
    void shouldReturn400WhenGivenInvalidExpirationDate() throws Exception {
        String invalidExpirationDateMessage = "Expiration date must be a future date (format: yyyy-MM-dd).";

        ShortenUrlRequest invalidRequest = new ShortenUrlRequest(VALID_ORIGINAL_URL, LocalDate.now().minusDays(5));

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
               .andExpect(jsonPath("$.message").value("Request validation failed for one or more fields"))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].message").value(invalidExpirationDateMessage));
    }


    @Test
    @DisplayName("GET /{shortCode} - Should return 302 when given existing short code")
    void shouldReturn302WhenGivenExistingShortCode() throws Exception {
        Url mockedUrl = Url.create(VALID_ORIGINAL_URL, VALID_SHORT_CODE, VALID_EXPIRES_AT);

        when(urlService.findByShortCode(VALID_SHORT_CODE)).thenReturn(mockedUrl);

        mockMvc.perform(get("/a1b2c3d"))
               .andExpect(status().isFound())
               .andExpect(header().string("Location", VALID_ORIGINAL_URL));
    }

    @Test
    @DisplayName("GET /{shortCode} - Should return 404 when given expired or non existent short code")
    void shouldReturn404WhenGivenExpiredOrNonExistingShortCode() throws Exception {
        String expiredShortCode = "expired";
        String notFoundErrorMessage =  "Short code %s does not exist or has expired.";

        when(urlService.findByShortCode(expiredShortCode))
                .thenThrow(new ShortCodeNotFoundException(expiredShortCode));

        mockMvc.perform(get("/" + expiredShortCode))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
               .andExpect(jsonPath("$.message").value(notFoundErrorMessage.formatted(expiredShortCode)));
    }

    @Test
    @DisplayName("GET / - Should return API metadata information")
    void shouldReturnApiMetadataInformation() throws Exception {
        String API_NAME = "URL Shortener API";
        String API_VERSION = "1.0.0";
        String API_STATUS = "operational";

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
