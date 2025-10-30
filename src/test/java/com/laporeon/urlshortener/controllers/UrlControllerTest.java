package com.laporeon.urlshortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.services.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static com.laporeon.urlshortener.commom.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("Should return 201 and URLResponseDTO when given valid request data")
     void shortenUrl_ShouldReturnCreatedWithUrlResponseDto() throws Exception {
        when(urlService.shortenUrl(any(UrlRequestDTO.class))).thenReturn(URL_RESPONSE_DTO);

        mockMvc.perform(post("/shorten-url")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(VALID_URL_REQUEST_DTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(URL_RESPONSE_DTO.shortUrl()))
               .andExpect(jsonPath("$.expiresAt").value(URL_RESPONSE_DTO.expiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
