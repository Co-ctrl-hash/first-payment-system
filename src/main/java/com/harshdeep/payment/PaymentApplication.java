package com.harshdeep.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Secure Payment Service.
 * A production-ready payment transaction management system with JWT authentication.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@SpringBootApplication
public class PaymentApplication {
    
    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
