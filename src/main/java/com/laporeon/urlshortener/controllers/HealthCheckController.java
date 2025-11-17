package com.laporeon.urlshortener.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
public class HealthCheckController {

    /**
     * Health check endpoint to verify if the service is up and running.
     * This endpoint is hidden from Swagger docs to avoid cluttering the API documentation.
     */
    @Hidden
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Health check accessed");

        return ResponseEntity.ok(Map.of(
                "status", "Service is UP",
                "path", "/health",
                "timestamp", Instant.now()
        ));
    }

}
