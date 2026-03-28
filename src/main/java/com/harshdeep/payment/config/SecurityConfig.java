package com.harshdeep.payment.config;

import com.harshdeep.payment.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 * Configures stateless HTTP session management and custom JWT filter.
 * 
 * Security Configuration:
 * - STATELESS session policy (no server-side session cookies)
 * - CORS disabled for API-only access
 * - CSRF disabled (appropriate for stateless JWT auth)
 * - Custom JWT filter for token validation
 * - Public endpoints: /auth/**, /
 * - Protected endpoints: /payments/**, /ai/**
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configure HTTP security with JWT authentication.
     * 
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security with JWT authentication");
        
        http
            // Disable CSRF - appropriate for stateless JWT authentication
            .csrf(csrf -> csrf.disable())
            
            // Use stateless session management - no server-side sessions
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure exception handling for authentication failures
            .exceptionHandling(ex -> 
                ex.authenticationEntryPoint(
                    (request, response, authException) -> {
                        log.warn("Unauthorized access attempt: {}", authException.getMessage());
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                )
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()           // Public: Registration & Login
                .requestMatchers("/").permitAll()                 // Public: Health check
                .requestMatchers("/actuator/health").permitAll()  // Public: Health endpoint (if Actuator added)
                .requestMatchers("/payments/**").authenticated()  // Protected: Payment endpoints
                .requestMatchers("/ai/**").authenticated()        // Protected: AI endpoints
                .anyRequest().authenticated()                     // All other requests require auth
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Disable frame options for H2 console (if used in development)
            // WARNING: H2 console should be disabled in production!
            .headers(headers -> 
                headers.frameOptions(frame -> frame.disable())
            );

        log.info("Spring Security configuration completed");
        return http.build();
    }
}
