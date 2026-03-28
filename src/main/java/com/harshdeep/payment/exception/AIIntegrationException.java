package com.harshdeep.payment.exception;

/**
 * Thrown when communication with the OpenAI API or external AI service fails.
 * 
 * This exception indicates a failure in integrating with external AI services,
 * such as:
 * - Network connectivity issues
 * - API timeouts or unavailability
 * - Invalid API responses
 * - Rate limiting by the AI service
 * 
 * This is an unchecked exception to indicate a runtime error condition
 * that typically cannot be recovered from within the application.
 * 
 * Returns HTTP 503 (SERVICE_UNAVAILABLE) to clients since the AI service
 * is temporarily unavailable.
 */
public class AIIntegrationException extends RuntimeException {

    /**
     * Creates a new AIIntegrationException with the provided message.
     * 
     * @param message descriptive message about the AI integration failure
     */
    public AIIntegrationException(String message) {
        super(message);
    }

    /**
     * Creates a new AIIntegrationException with message and underlying cause.
     * 
     * @param message descriptive message about the AI integration failure
     * @param cause the underlying exception that caused this error
     */
    public AIIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
