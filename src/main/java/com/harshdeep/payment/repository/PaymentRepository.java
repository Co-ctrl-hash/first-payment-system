package com.harshdeep.payment.repository;

import com.harshdeep.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Payment entity operations.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find all payments for a specific user.
     * 
     * @param userId the user ID
     * @return list of payments for the user
     */
    List<Payment> findByUserId(Long userId);
}
