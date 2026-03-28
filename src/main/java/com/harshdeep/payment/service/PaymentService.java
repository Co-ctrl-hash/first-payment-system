package com.harshdeep.payment.service;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.entity.PaymentStatus;
import com.harshdeep.payment.exception.PaymentOperationException;
import com.harshdeep.payment.exception.ResourceNotFoundException;
import com.harshdeep.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service layer for payment transaction management.
 * Handles business logic for payment creation, retrieval, and refund processing.
 * 
 * Payment Processing Rules:
 * - Amounts exceeding 100,000 automatically fail
 * - 75% success rate for valid amounts
 * - Only successful payments can be refunded
 */
@Slf4j
@Service
@Transactional
public class PaymentService {

    private static final double SUCCESS_RATE = 0.75;
    private static final double MAX_AMOUNT = 100_000.0;
    private static final String TRANSACTION_ID_FORMAT = "HD-%d-%d";
    private static final String AMOUNT_EXCEEDED_MESSAGE = "Payment amount exceeds maximum allowed limit: %.2f";
    private static final String REFUND_FAILED_MESSAGE = "Only successful payments can be refunded. Current status: %s";

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Create a new payment transaction with automatic status determination.
     * 
     * @param payment Payment entity with user ID, amount, currency, and method
     * @return Saved payment with generated transaction ID and determined status
     * @throws IllegalArgumentException if payment is null or invalid
     */
    public Payment createPayment(@NonNull Payment payment) {
        log.info("Creating payment for user ID: {}, Amount: {}", payment.getUserId(), payment.getAmount());
        
        validatePayment(payment);
        
        payment.setCreatedAt(LocalDateTime.now());
        
        // Generate unique transaction ID
        String transactionId = String.format(TRANSACTION_ID_FORMAT, 
                payment.getUserId(), 
                System.currentTimeMillis());
        payment.setTransactionId(transactionId);
        
        // Determine payment status based on business rules
        determinePaymentStatus(payment);
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully - Transaction ID: {}, Status: {}", 
                transactionId, savedPayment.getStatus());
        
        return savedPayment;
    }

    /**
     * Retrieve all payments in the system.
     * 
     * @return List of all payments
     */
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        log.debug("Retrieving all payments");
        List<Payment> payments = paymentRepository.findAll();
        log.info("Retrieved {} payments from database", payments.size());
        return payments;
    }

    /**
     * Retrieve a specific payment by ID.
     * 
     * @param id Payment ID
     * @return Payment entity
     * @throws ResourceNotFoundException if payment not found
     */
    @Transactional(readOnly = true)
    public Payment getPayment(@NonNull Long id) {
        log.debug("Retrieving payment with ID: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment not found with ID: {}", id);
                    return new ResourceNotFoundException("Payment not found with id: " + id);
                });
    }

    /**
     * Retrieve all payments for a specific user.
     * 
     * @param userId User ID
     * @return List of payments for the user
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(@NonNull Long userId) {
        log.debug("Retrieving all payments for user ID: {}", userId);
        List<Payment> payments = paymentRepository.findByUserId(userId);
        log.info("Found {} payment(s) for user ID: {}", payments.size(), userId);
        return payments;
    }

    /**
     * Process refund for a successfully completed payment.
     * Only payments with SUCCESS status can be refunded.
     * 
     * @param id Payment ID to refund
     * @return Refunded payment with updated status
     * @throws ResourceNotFoundException if payment not found
     * @throws PaymentOperationException if payment cannot be refunded
     */
    public Payment refund(@NonNull Long id) {
        log.info("Processing refund for payment ID: {}", id);
        Payment payment = getPayment(id);
        
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            log.error("Refund failed: Payment {} has status {}", id, payment.getStatus());
            throw new PaymentOperationException(
                    String.format(REFUND_FAILED_MESSAGE, payment.getStatus())
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRemarks("Payment refunded successfully");
        Payment refundedPayment = paymentRepository.save(payment);
        
        log.info("Payment {} refunded successfully", id);
        return refundedPayment;
    }

    /**
     * Validate payment entity for required fields.
     * 
     * @param payment Payment to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePayment(@NonNull Payment payment) {
        if (payment.getUserId() == null || payment.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    /**
     * Determine payment status based on business rules:
     * 1. If amount exceeds MAX_AMOUNT, status is FAILED
     * 2. Otherwise, 75% chance of SUCCESS, 25% chance of FAILED
     * 
     * @param payment Payment to update with status and remarks
     */
    private void determinePaymentStatus(@NonNull Payment payment) {
        payment.setStatus(PaymentStatus.INITIATED);
        
        // Business rule: Amounts over 100,000 automatically fail
        if (payment.getAmount() > MAX_AMOUNT) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRemarks(String.format(AMOUNT_EXCEEDED_MESSAGE, MAX_AMOUNT));
            log.warn("Payment {} failed - Amount exceeds limit: {}", 
                    payment.getTransactionId(), payment.getAmount());
            return;
        }
        
        // Simulate 75% success rate
        if (ThreadLocalRandom.current().nextDouble() < SUCCESS_RATE) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setRemarks("Payment processed successfully");
            log.info("Payment {} completed successfully", payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRemarks("Payment failed due to insufficient funds or technical error");
            log.warn("Payment {} failed - Random failure simulation", payment.getTransactionId());
        }
    }
}
        return refundedPayment;
    }
}
