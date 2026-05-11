package com.laporeon.urlshortener.controllers.docs;

import com.laporeon.urlshortener.dtos.response.ErrorResponse;
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
        summary = "Redirect to original URL.",
        description = "Receives a short code and redirects to the original URL if found and not expired.\n\n" +
                "**Note:** This endpoint will not work properly in Swagger UI due to CORS limitations with redirects. " +
                "Please test directly in your browser or using your preferred REST Client.",
        responses = {
                @ApiResponse(responseCode = "302", description = "Short code found"),
                @ApiResponse(responseCode = "404", description = "Resource not found",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(value = SwaggerDocsConstants.NOT_FOUND_ERROR_RESPONSE))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = @ExampleObject(value = SwaggerDocsConstants.INTERNAL_ERROR_RESPONSE)))
        }
)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedirectDocumentation {
}
