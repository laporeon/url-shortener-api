package com.laporeon.urlshortener.commom;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Constants {

    public static final String BASE_URL = "https://localhost:8080";

    public static final UrlRequestDTO VALID_URL_REQUEST_DTO = new UrlRequestDTO(
            "https://www.google.com/",
            LocalDate.now().plusDays(15));

    public static final UrlRequestDTO INVALID_URL_REQUEST_DTO = new UrlRequestDTO(
            "urlinvalida",
            LocalDate.now().plusDays(2));

    public static final Url SAVED_URL_ENTITY = new Url(
            "69026cdf278513d576c67059",
            "a1b2c3d",
            VALID_URL_REQUEST_DTO.originalUrl(),
            VALID_URL_REQUEST_DTO.expirationDate().atTime(23,00,00),
            LocalDateTime.of(2025, 10, 29, 13, 36, 24));

    public static final UrlResponseDTO URL_RESPONSE_DTO = new UrlResponseDTO(
            BASE_URL + "/" + SAVED_URL_ENTITY.getShortCode(),
            SAVED_URL_ENTITY.getExpiresAt());

    public static final String VALID_SHORT_CODE = SAVED_URL_ENTITY.getShortCode();

    public static final String EXPIRED_SHORT_CODE = "qAzScFb";
}
