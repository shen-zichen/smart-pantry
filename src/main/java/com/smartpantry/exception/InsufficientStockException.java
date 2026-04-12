package com.smartpantry.exception;

/** Thrown when a consume operation exceeds available stock. */
public class InsufficientStockException extends RuntimeException {
  public InsufficientStockException(String message) {
    super(message);
  }
}
