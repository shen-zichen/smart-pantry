package com.smartpantry.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Catches exceptions thrown by any REST controller and converts them to
 * consistent JSON error
 * responses with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 404 — resource not found. */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
    return buildError("Not Found", ex.getMessage());
  }

  /** 409 — conflict, e.g. insufficient stock. */
  @ExceptionHandler(InsufficientStockException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConflict(InsufficientStockException ex) {
    return buildError("Conflict", ex.getMessage());
  }

  /** 400 — validation errors from @Valid on request DTOs. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    String details = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return buildError("Validation Failed", details);
  }

  /** 400 — bad request params (wrong type, missing required param, etc.) */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(IllegalArgumentException ex) {
    return buildError("Bad Request", ex.getMessage());
  }

  /** 400 — missing required query parameter. */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleMissingParam(MissingServletRequestParameterException ex) {
    return buildError("Bad Request", ex.getMessage());
  }

  /**
   * 500 — database/JPA errors (covers TransientObjectException,
   * DataIntegrityViolation, etc.)
   */
  @ExceptionHandler(DataAccessException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleDataAccess(DataAccessException ex) {
    return buildError("Database Error", ex.getMostSpecificCause().getMessage());
  }

  /**
   * 500 — catch-all for any unhandled exceptions so stack traces never leak to
   * the frontend.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneral(Exception ex) {
    return buildError("Internal Server Error", ex.getMessage());
  }

  /** Builds a consistent error response shape. */
  private Map<String, Object> buildError(String error, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("error", error);
    body.put("message", message);
    body.put("timestamp", LocalDateTime.now().toString());
    return body;
  }
}
