package com.laporeon.urlshortener.exceptions;

import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult()
          .getFieldErrors()
          .forEach(fieldError ->
                           details.put(fieldError.getField(), fieldError.getDefaultMessage())
          );

        ErrorResponseDTO error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                details);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {

        Map<String, String> details = new HashMap<>();
        details.put("shortCode", ex.getMessage());

        ErrorResponseDTO error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                details);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponseDTO error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                 null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponseDTO buildErrorResponse(HttpStatus httpStatus, String title, Map<String, String> details) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.name(),
                title,
                details,
                Instant.now()
        );
    }
}
