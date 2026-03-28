package com.harshdeep.payment.service;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.entity.PaymentStatus;
import com.harshdeep.payment.exception.ResourceNotFoundException;
import com.harshdeep.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_shouldFail_whenAmountExceedsLimit() {
        Payment payment = new Payment();
        payment.setUserId(1L);
        payment.setAmount(100001.0);
        payment.setCurrency("USD");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(payment);

        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertEquals("Payment amount exceeds maximum allowed limit", result.getRemarks());
        assertNotNull(result.getTransactionId());
        assertNotNull(result.getCreatedAt());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPayment_shouldThrowNotFound_whenPaymentMissing() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPayment(99L));
    }

    @Test
    void refund_shouldThrow_whenPaymentIsNotSuccessful() {
        Payment payment = new Payment();
        payment.setId(10L);
        payment.setStatus(PaymentStatus.FAILED);
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> paymentService.refund(10L));

        assertEquals("Only successful payments can be refunded", ex.getMessage());
    }

    @Test
    void refund_shouldMarkPaymentRefunded_whenPaymentIsSuccessful() {
        Payment payment = new Payment();
        payment.setId(11L);
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(11L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.refund(11L);

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        assertEquals("Payment refunded successfully", result.getRemarks());
        verify(paymentRepository).save(payment);
    }
}
