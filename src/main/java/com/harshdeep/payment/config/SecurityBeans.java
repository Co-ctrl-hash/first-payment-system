package com.harshdeep.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for security-related beans.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Configuration
public class SecurityBeans {

    /**
     * BCrypt password encoder bean for secure password hashing.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
