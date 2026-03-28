package com.harshdeep.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Payment entity representing a payment transaction in the system.
 * 
 * Each payment tracks the transaction between a user and the payment system,
 * including the amount, status, and relevant metadata.
 * 
 * Validation is performed at both entity and controller levels to ensure
 * data integrity at the database and API boundaries.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    /**
     * Unique identifier for the payment (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID associated with this payment (required).
     */
    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    private Long userId;

    /**
     * Payment amount in the specified currency (required, must be positive).
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    @DecimalMin(value = "0.01", message = "Minimum payment amount is 0.01")
    @DecimalMax(value = "999999.99", message = "Maximum payment amount is 999999.99")
    private Double amount;

    /**
     * Currency code for the payment (e.g., USD, EUR, INR).
     */
    @NotBlank(message = "Currency code cannot be blank")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    private String currency;

    /**
     * Payment method used (e.g., CREDIT_CARD, DEBIT_CARD, UPI).
     */
    @NotBlank(message = "Payment method cannot be blank")
    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod;

    /**
     * Current status of the payment transaction.
     * 
     * Possible values: INITIATED, SUCCESS, FAILED, REFUNDED
     */
    @NotNull(message = "Payment status cannot be null")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /**
     * Unique transaction identifier for this payment.
     * Format: HD-{userId}-{timestamp}
     */
    @NotBlank(message = "Transaction ID cannot be blank")
    @Column(unique = true)
    private String transactionId;

    /**
     * Optional remarks or notes about the payment (e.g., failure reason).
     */
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    /**
     * Timestamp when this payment record was created.
     * Automatically set by Hibernate at insert time.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
