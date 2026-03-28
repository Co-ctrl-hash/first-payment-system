package com.harshdeep.payment.exception;

/**
 * Exception thrown when a payment operation fails (e.g., refund for non-successful payment).
 * Used for business logic violations in payment processing.
 */
public class PaymentOperationException extends RuntimeException {
    
    public PaymentOperationException(String message) {
        super(message);
    }

    public PaymentOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
