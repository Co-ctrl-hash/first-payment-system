package com.harshdeep.payment.exception;

import lombok.*;

/**
 * Standard error response DTO for REST API error responses.
 * 
 * Provides consistent error information to API clients including:
 * - Error message describing what went wrong
 * - HTTP status code for the error
 * - Server timestamp when the error occurred
 * 
 * This class is used by GlobalExceptionHandler to format all API errors.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    
    /**
     * Human-readable error message describing the issue.
     * Should be specific enough for API clients to understand the problem,
     * but not expose internal implementation details.
     */
    private String message;
    
    /**
     * HTTP status code of the error response.
     * Examples: 400 (Bad Request), 404 (Not Found), 500 (Internal Server Error)
     */
    private int status;
    
    /**
     * Server-side timestamp (milliseconds since epoch) when the error occurred.
     * Useful for logging, debugging, and client-side tracking.
     */
    private long timestamp;
}
