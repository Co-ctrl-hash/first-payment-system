package com.harshdeep.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 * Provides centralized exception handling and consistent error response format.
 * All exceptions are formatted as ErrorResponse with timestamp, status, and message.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException.
     * Returns 404 NOT_FOUND with friendly error message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle PaymentOperationException.
     * Returns 400 BAD_REQUEST for payment operation violations.
     */
    @ExceptionHandler(PaymentOperationException.class)
    public ResponseEntity<ErrorResponse> handlePaymentOperationException(PaymentOperationException ex) {
        log.warn("Payment operation failed: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ASIIntegrationException.
     * Returns 503 SERVICE_UNAVAILABLE when AI service fails.
     */
    @ExceptionHandler(AIIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleAIIntegrationException(AIIntegrationException ex) {
        log.error("AI integration error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
                "AI service temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handle validation errors from @Valid annotation.
     * Returns 400 BAD_REQUEST with field-specific error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", message);
        
        ErrorResponse error = new ErrorResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ResponseStatusException from Spring.
     * Preserves the exception's HTTP status code.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Request failed";
        int status = ex.getStatusCode().value();
        
        log.warn("Response status exception - Status: {}, Message: {}", status, message);
        
        ErrorResponse error = new ErrorResponse(
                message,
                status,
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    /**
     * Handle database integrity violations (FK constraints, unique violations, etc.).
     * Returns 409 CONFLICT.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                "Data conflict or constraint violation",
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handle IllegalArgumentException from business logic validation.
     * Returns 400 BAD_REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Generic exception handler for all unhandled exceptions.
     * Returns 500 INTERNAL_SERVER_ERROR.
     * This should only be reached for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred. Please contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
