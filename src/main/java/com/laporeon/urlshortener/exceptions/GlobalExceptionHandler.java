package com.laporeon.urlshortener.exceptions;

import com.laporeon.urlshortener.dtos.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
                                  .getFieldErrors()
                                  .stream()
                                  .map(FieldError::getDefaultMessage)
                                  .toList();

        ErrorResponseDTO error = buildErrorResponse(HttpStatus.BAD_REQUEST, messages);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleShortCodeNotFoundException(ShortCodeNotFoundException ex) {
        ErrorResponseDTO error = buildErrorResponse(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private ErrorResponseDTO buildErrorResponse(HttpStatus httpStatus, List<String> messages) {
        return new ErrorResponseDTO(
                httpStatus.value(),
                httpStatus.name(),
                messages,
                Instant.now()
        );
    }
}
