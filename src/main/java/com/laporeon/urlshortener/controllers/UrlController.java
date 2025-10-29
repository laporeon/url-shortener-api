package com.laporeon.urlshortener.controllers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.services.UrlService;
import com.laporeon.urlshortener.utils.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "URL")
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @Operation(
            summary = "Shorten a URL",
            description = "Receives a long URL and an optional expiration date. Returns a shortened URL.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UrlRequestDTO.class),
                            examples = @ExampleObject(value = SwaggerConstants.URL_REQUEST_EXAMPLE)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerConstants.URL_RESPONSE_EXAMPLE))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerConstants.VALIDATION_ERROR_MESSAGE)))
            }
    )
    @PostMapping("/shorten-url")
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlRequestDTO dto) {
        UrlResponseDTO urlResponseDTO = urlService.shortenUrl(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponseDTO);
    }

    @Operation(
            summary = "Redirect to original URL.",
            description = "Receives a short code and redirects to the original URL if found and not expired.",
            responses = {
                    @ApiResponse(responseCode = "301", description = "Moved Permanently"),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerConstants.NOT_FOUND_ERROR_MESSAGE)))
            }
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable("shortCode") String shortCode) {
        log.info("Short Code requested: {}", shortCode);

        Url url = urlService.findByShortCode(shortCode);

        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(URI.create(url.getOriginalUrl()));

        log.info("Redirecting to URL: {}", url.getOriginalUrl());

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();

    }
}
