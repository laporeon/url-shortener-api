package com.laporeon.urlshortener.controllers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.entities.Url;
import com.laporeon.urlshortener.services.UrlService;
import com.laporeon.urlshortener.utils.SwaggerConstants;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlRequestDTO dto, HttpServletRequest request) {
        UrlResponseDTO urlResponseDTO = urlService.shortenUrl(dto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponseDTO);
    }

    @Operation(
            summary = "Redirect to original URL.",
            description = "Receives a short code and redirects to the original URL if found and not expired.\n\n" +
                    "**Note:** This endpoint will not work properly in Swagger UI due to CORS limitations with redirects. " +
                    "Please test directly in your browser or using your preferred REST Client.",
            responses = {
                    @ApiResponse(responseCode = "301", description = "Moved Permanently"),
                    @ApiResponse(responseCode = "404", description = "Not Found",
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

        URI location = UriComponentsBuilder.fromUriString(url.getOriginalUrl()).build().toUri();

        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(location);

        log.info("Redirecting to URL: {}", url.getOriginalUrl());

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();

    }

    /**
     * Redirects the root URL ("/") to Swagger UI documentation page.
     * This endpoint is hidden from Swagger docs to avoid cluttering the API documentation.
     */
    @Hidden
    @GetMapping("/")
    public RedirectView redirectHomeToSwaggerDocs() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
