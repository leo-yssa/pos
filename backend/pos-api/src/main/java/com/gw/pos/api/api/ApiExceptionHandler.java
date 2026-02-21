package com.gw.pos.api.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> handleStatus(ResponseStatusException e) {
    return ResponseEntity.status(e.getStatusCode())
        .body(Map.of("timestamp", Instant.now().toString(), "error", e.getReason()));
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<?> handleEmpty(EmptyResultDataAccessException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("timestamp", Instant.now().toString(), "error", "NOT_FOUND"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
    return ResponseEntity.badRequest()
        .body(Map.of("timestamp", Instant.now().toString(), "error", "VALIDATION_FAILED"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest()
        .body(Map.of("timestamp", Instant.now().toString(), "error", e.getMessage()));
  }
}

