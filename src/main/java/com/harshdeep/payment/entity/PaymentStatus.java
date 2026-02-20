package com.harshdeep.payment.entity;

/**
 * Enum representing different payment statuses in the transaction lifecycle.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
public enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED,
    REFUNDED
}
