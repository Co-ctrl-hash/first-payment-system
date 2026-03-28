package com.harshdeep.payment.controller;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for payment management endpoints.
 * Handles CRUD operations and refund processing for payment transactions.
 * All endpoints require JWT authentication.
 */
@Slf4j
@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Create a new payment transaction.
     * 
     * @param payment Payment object with required details
     * @return Created payment with generated transaction ID and status
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        log.info("Creating payment for user: {}, Amount: {}", payment.getUserId(), payment.getAmount());
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    /**
     * Retrieve all payments in the system.
     * 
     * @return List of all payment transactions
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = paymentService.getAllPayments();
        log.debug("Total payments retrieved: {}", payments.size());
        return ResponseEntity.ok(payments);
    }

    /**
     * Retrieve a specific payment by ID.
     * 
     * @param id Payment ID
     * @return Payment transaction details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Retrieve all payments for a specific user.
     * 
     * @param userId User ID
     * @return List of payments for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable Long userId) {
        log.info("Fetching payments for user: {}", userId);
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        log.debug("Total payments for user {}: {}", userId, payments.size());
        return ResponseEntity.ok(payments);
    }

    /**
     * Process refund for a successfully completed payment.
     * Only successful payments can be refunded.
     * 
     * @param id Payment ID to refund
     * @return Refunded payment with updated status
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        log.info("Processing refund for payment ID: {}", id);
        Payment refundedPayment = paymentService.refund(id);
        return ResponseEntity.ok(refundedPayment);
    }
}
