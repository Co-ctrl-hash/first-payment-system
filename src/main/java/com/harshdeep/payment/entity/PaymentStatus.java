package com.harshdeep.payment.entity;

/**
 * Enumeration of possible payment transaction statuses.
 * 
 * Represents the lifecycle states of a payment transaction in the system.
 * Each status represents a distinct phase in the payment workflow.
 */
public enum PaymentStatus {
    
    /**
     * Initial state when a payment transaction is first created.
     * The payment has been recorded in the system but processing has not yet occurred.
     */
    INITIATED,
    
    /**
     * The payment transaction has been successfully processed.
     * Funds have been transferred and the transaction is complete.
     * This is the target state for successful payments.
     */
    SUCCESS,
    
    /**
     * The payment transaction has failed.
     * This can occur due to insufficient funds, invalid credentials, or system errors.
     * Failed payments cannot be refunded (they never succeeded in the first place).
     */
    FAILED,
    
    /**
     * A previously successful payment has been refunded.
     * This status indicates that funds were returned to the original source.
     * Only SUCCESS payments can transition to REFUNDED state.
     */
    REFUNDED
}
