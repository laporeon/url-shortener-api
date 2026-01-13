package com.laporeon.urlshortener.controllers;

import com.laporeon.urlshortener.dtos.request.UrlRequestDTO;
import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import com.laporeon.urlshortener.dtos.response.UrlResponseDTO;
import com.laporeon.urlshortener.services.UrlService;
import com.laporeon.urlshortener.utils.SwaggerExamples;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
                            examples = @ExampleObject(value = SwaggerExamples.CREATE_SHORT_URL_REQUEST)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully Shorten URL",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.SHORTEN_URL_SUCCESS_RESPONSE))),
                    @ApiResponse(responseCode = "400", description = "Request validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.VALIDATION_ERROR_RESPONSE))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_ERROR_RESPONSE)))
            }
    )
    @PostMapping("/shorten")
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
                    @ApiResponse(responseCode = "302", description = "Short code found"),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND_ERROR_RESPONSE))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_ERROR_RESPONSE)))
            }
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable("shortCode") String shortCode) {
        String originalUrl = urlService.findByShortCode(shortCode).getOriginalUrl();

        log.info("Redirecting | shortCode={} | target={}", shortCode, originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();

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
