package com.harshdeep.payment.controller;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for payment transaction operations.
 * All endpoints require JWT authentication except /auth/**.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService service;

    /**
     * Constructor injection for PaymentService.
     * 
     * @param service the payment service
     */
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    /**
     * Create a new payment transaction.
     * 
     * @param payment the payment details
     * @return the created payment with transaction ID and status
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        log.info("POST /payments - Creating new payment");
        Payment createdPayment = service.createPayment(payment);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    /**
     * Retrieve all payments in the system.
     * 
     * @return list of all payments
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("GET /payments - Fetching all payments");
        List<Payment> payments = service.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * Retrieve a specific payment by ID.
     * 
     * @param id the payment ID
     * @return the payment details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.info("GET /payments/{} - Fetching payment", id);
        Payment payment = service.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Retrieve all payments for a specific user.
     * 
     * @param userId the user ID
     * @return list of payments for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable Long userId) {
        log.info("GET /payments/user/{} - Fetching payments for user", userId);
        List<Payment> payments = service.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Process a refund for a successful payment.
     * 
     * @param id the payment ID to refund
     * @return the refunded payment
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        log.info("POST /payments/{}/refund - Processing refund", id);
        Payment refundedPayment = service.refund(id);
        return ResponseEntity.ok(refundedPayment);
    }
}
