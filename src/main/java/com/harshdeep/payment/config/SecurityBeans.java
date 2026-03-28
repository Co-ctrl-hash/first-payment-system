package com.harshdeep.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for security-related beans and HTTP client setup.
 * Provides BCryptPasswordEncoder for password hashing and RestTemplate for external API calls.
 */
@Slf4j
@Configuration
public class SecurityBeans {

    private static final int BCRYPT_STRENGTH = 10;
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 15000;

    /**
     * Create BCryptPasswordEncoder bean for password encryption.
     * Uses default strength of 10 for password hashing.
     * 
     * @return BCryptPasswordEncoder configured with standard strength
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.info("Creating BCryptPasswordEncoder with strength: {}", BCRYPT_STRENGTH);
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    /**
     * Create RestTemplate bean for making HTTP requests to external services.
     * Configured with connection and read timeouts to prevent hanging requests.
     * 
     * @param builder RestTemplateBuilder for building RestTemplate
     * @return RestTemplate with connection and read timeouts configured
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Creating RestTemplate with timeouts - Connect: {}ms, Read: {}ms", 
                CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
        
        return builder
                .setConnectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MS))
                .setReadTimeout(Duration.ofMillis(READ_TIMEOUT_MS))
                .build();
    }
}
