package com.laporeon.urlshortener.controllers.docs;

import com.laporeon.urlshortener.dtos.request.ShortenUrlRequest;
import com.laporeon.urlshortener.dtos.response.ErrorResponse;
import com.laporeon.urlshortener.dtos.response.ShortenUrlResponse;
import com.laporeon.urlshortener.dtos.response.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
        summary = "Shorten a URL",
        description = "Receives a long URL and an optional expiration date. Returns a shortened URL.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Request payload",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ShortenUrlRequest.class),
                        examples = @ExampleObject(value = SwaggerDocsConstants.SHORTEN_URL_REQUEST)
                )
        ),
        responses = {
                @ApiResponse(responseCode = "201", description = "Successfully Shorten URL",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ShortenUrlResponse.class),
                                examples = @ExampleObject(value = SwaggerDocsConstants.SHORTEN_URL_SUCCESS_RESPONSE))),
                @ApiResponse(responseCode = "400", description = "Request validation failed",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ValidationErrorResponse.class),
                                examples = @ExampleObject(value = SwaggerDocsConstants.VALIDATION_ERROR_RESPONSE))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(value = SwaggerDocsConstants.INTERNAL_ERROR_RESPONSE)))
        }
)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShortenUrlDocumentation {
}
