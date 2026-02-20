package com.harshdeep.payment.config;

import com.harshdeep.payment.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 * Configures stateless session management and endpoint security.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Constructor injection for JwtFilter.
     * 
     * @param jwtFilter JWT authentication filter
     */
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configure HTTP security with JWT authentication.
     * - Public endpoints: /auth/**, /h2-console/**
     * - Protected endpoints: All others require authentication
     * - Stateless session management
     * 
     * @param http HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
