package com.laporeon.urlshortener.exceptions;

import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import com.laporeon.urlshortener.dtos.response.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed | method={} | path={} | errors={}",
                 request.getMethod(),
                 request.getRequestURI(),
                 ex.getBindingResult().getErrorCount());

        List<Map<String, String>> errors = ex.getBindingResult()
                                             .getFieldErrors()
                                             .stream()
                                             .map(err -> Map.of(
                                                     "field", err.getField(),
                                                     "message", err.getDefaultMessage()))
                                             .toList();


        ValidationErrorResponseDTO error = new ValidationErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Request validation failed for one or more fields",
                errors,
                Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND_ERROR",
                ex.getMessage(),
                Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error occurred | method={} | path={} | exception={} | message={}",
                  request.getMethod(),
                  request.getRequestURI(),
                  ex.getClass().getSimpleName(),
                  ex.getMessage(),
                  ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                 Instant.now());

        return ResponseEntity.internalServerError().body(error);
    }

}
