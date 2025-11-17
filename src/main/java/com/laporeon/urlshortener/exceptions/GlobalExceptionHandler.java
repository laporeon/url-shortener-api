package com.laporeon.urlshortener.exceptions;

import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> messages = new HashMap<>();
        ex.getBindingResult()
          .getFieldErrors()
          .forEach(fieldError ->
                           messages.put(fieldError.getField(), fieldError.getDefaultMessage())
          );

        ErrorResponseDTO error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                messages);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {

        Map<String, String> messages = new HashMap<>();
        messages.put("shortCode", ex.getMessage());

        ErrorResponseDTO error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                messages);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
