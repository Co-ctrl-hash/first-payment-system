package com.harshdeep.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entry point for the Payment Transaction System application.
 * 
 * This Spring Boot application provides:
 * - JWT-based authentication and authorization
 * - Payment transaction management with JPA persistence
 * - GenAI integration for chat-based payment analytics
 * - RESTful API with Global Exception Handling
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class PaymentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
