package com.harshdeep.payment.controller;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        log.info("POST /payments - Creating new payment");
        Payment createdPayment = service.createPayment(payment);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("GET /payments - Fetching all payments");
        List<Payment> payments = service.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.info("GET /payments/{} - Fetching payment", id);
        Payment payment = service.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@PathVariable Long userId) {
        log.info("GET /payments/user/{} - Fetching payments for user", userId);
        List<Payment> payments = service.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        log.info("POST /payments/{}/refund - Processing refund", id);
        Payment refundedPayment = service.refund(id);
        return ResponseEntity.ok(refundedPayment);
    }
}
