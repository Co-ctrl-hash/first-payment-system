package com.harshdeep.payment.service;

import com.harshdeep.payment.entity.*;
import com.harshdeep.payment.exception.ResourceNotFoundException;
import com.harshdeep.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final double SUCCESS_RATE = 0.75; // 75% success rate

    private final PaymentRepository repository;
    private final Random random = new Random();

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment createPayment(Payment payment) {
        log.info("Creating payment for user ID: {}, Amount: {} {}", 
                payment.getUserId(), payment.getAmount(), payment.getCurrency());

        payment.setCreatedAt(LocalDateTime.now());
        
        // Generate custom transaction ID: HD-{userId}-{timestamp}
        String transactionId = String.format("HD-%d-%d", 
                payment.getUserId(), 
                System.currentTimeMillis());
        payment.setTransactionId(transactionId);
        
        payment.setStatus(PaymentStatus.INITIATED);
        log.info("Transaction initiated with ID: {}", transactionId);

        // Business rule: Amounts over 100,000 automatically fail
        if (payment.getAmount() > 100000) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRemarks("Payment amount exceeds maximum allowed limit");
            log.warn("Payment {} failed - Amount exceeds limit: {}", transactionId, payment.getAmount());
        }
        // Determine payment status: 75% SUCCESS, 25% FAILED
        else if (random.nextDouble() < SUCCESS_RATE) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setRemarks("Payment processed successfully");
            log.info("Payment {} completed successfully", transactionId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setRemarks("Payment failed due to insufficient funds or technical error");
            log.warn("Payment {} failed", transactionId);
        }

        Payment savedPayment = repository.save(payment);
        log.info("Payment saved to database with ID: {}, Status: {}", 
                savedPayment.getId(), savedPayment.getStatus());
        
        return savedPayment;
    }

    public List<Payment> getAllPayments() {
        log.info("Retrieving all payments");
        return repository.findAll();
    }

    public Payment getPayment(Long id) {
        log.info("Retrieving payment with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found with ID: {}", id);
                    return new ResourceNotFoundException("Payment not found with id: " + id);
                });
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        log.info("Retrieving all payments for user ID: {}", userId);
        List<Payment> payments = repository.findByUserId(userId);
        log.info("Found {} payment(s) for user ID: {}", payments.size(), userId);
        return payments;
    }

    public Payment refund(Long id) {
        log.info("Processing refund for payment ID: {}", id);
        Payment payment = getPayment(id);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            log.error("Refund failed: Payment {} has status {}", id, payment.getStatus());
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRemarks("Payment refunded successfully");
        Payment refundedPayment = repository.save(payment);
        
        log.info("Payment {} refunded successfully", id);
        return refundedPayment;
    }
}
