package com.laporeon.urlshortener.exceptions;

import com.laporeon.urlshortener.dtos.response.ErrorResponse;
import com.laporeon.urlshortener.dtos.response.ValidationErrorResponse;
import com.laporeon.urlshortener.dtos.response.ValidationErrorResponse.FieldError;
import com.laporeon.urlshortener.exceptions.custom.ShortCodeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed | method={} | path={} | errors={}",
                 request.getMethod(),
                 request.getRequestURI(),
                 ex.getBindingResult().getErrorCount());

        List<FieldError> errors = ex.getBindingResult()
                                    .getFieldErrors()
                                    .stream()
                                    .map(err -> new FieldError(err.getField(), err.getDefaultMessage()))
                                    .toList();


        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Request validation failed for one or more fields",
                errors,
                Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND_ERROR",
                ex.getMessage(),
                Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error occurred | method={} | path={} | exception={} | message={}",
                  request.getMethod(),
                  request.getRequestURI(),
                  ex.getClass().getSimpleName(),
                  ex.getMessage(),
                  ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                 Instant.now());

        return ResponseEntity.internalServerError().body(error);
    }
}
