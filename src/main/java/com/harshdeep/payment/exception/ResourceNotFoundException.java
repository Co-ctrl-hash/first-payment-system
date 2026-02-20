package com.harshdeep.payment.exception;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor with error message.
     * 
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
