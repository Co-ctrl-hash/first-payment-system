package com.harshdeep.payment.repository;

import com.harshdeep.payment.entity.Payment;
import com.harshdeep.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Payment entity persistence operations.
 * 
 * Extends JpaRepository to provide CRUD operations plus custom query methods
 * for payment analytics and filtering.
 * 
 * All methods are transactional by default through Spring Data JPA.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Retrieves all payments made by a specific user.
     * 
     * @param userId the user ID to filter by
     * @return list of payments for the given user (empty list if no payments found)
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Retrieves all payments with a specific status.
     * 
     * @param status the payment status to filter by (INITIATED, SUCCESS, FAILED, REFUNDED)
     * @return list of payments with the given status (empty list if no payments found)
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Counts the total number of payments with a specific status.
     * 
     * Useful for analytics: "How many payments succeeded?", "How many failed?"
     * 
     * @param status the payment status to count
     * @return the count of payments with the given status
     */
    long countByStatus(PaymentStatus status);

    /**
     * Calculates the total amount of all payments with a specific status.
     * 
     * Useful for financial analytics: "What is the total amount of successful payments?"
     * 
     * Returns 0.0 if no payments with the given status exist.
     * 
     * @param status the payment status to filter by
     * @return the sum of amounts for all payments with the given status (0.0 if none found)
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    Double sumAmountByStatus(@Param("status") PaymentStatus status);
}
