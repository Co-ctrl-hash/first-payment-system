package com.harshdeep.payment.exception;

/**
 * Thrown when a requested resource cannot be found in the system.
 * 
 * This is an unchecked exception (extends RuntimeException) to indicate
 * a runtime error condition rather than a recoverable error.
 * 
 * Typical usage: When querying for a Payment by ID or User that doesn't exist.
 * Returns HTTP 404 (NOT_FOUND) to clients.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Creates a new ResourceNotFoundException with the provided message.
     * 
     * @param message descriptive message about what resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Creates a new ResourceNotFoundException with message and underlying cause.
     * 
     * @param message descriptive message about what resource was not found
     * @param cause the underlying exception that caused this error
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Convenience factory method to create ResourceNotFoundException for entity lookup failures.
     * 
     * @param entityName the type of entity being looked up (e.g., "Payment", "User")
     * @param id the identifier that was not found
     * @return constructed ResourceNotFoundException with standard message format
     */
    public static ResourceNotFoundException forEntity(String entityName, Object id) {
        return new ResourceNotFoundException(entityName + " not found with id: " + id);
    }
}
