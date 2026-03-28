package com.harshdeep.payment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility component for JWT token generation, validation, and claim extraction.
 * Provides secure token creation and validation for stateless authentication.
 */
@Slf4j
@Component
public class JwtUtil {

    private static final int MIN_SECRET_LENGTH = 32;
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    /**
     * Validates JWT secret during post-construction.
     * Ensures the secret key is at least 32 characters long for HS256 algorithm.
     * 
     * @throws IllegalStateException if secret is null or too short
     */
    @PostConstruct
    void validateSecretLength() {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            log.error("JWT secret validation failed: must be at least {} characters", MIN_SECRET_LENGTH);
            throw new IllegalStateException(
                    "jwt.secret must be at least " + MIN_SECRET_LENGTH + " characters long"
            );
        }
        log.info("JWT secret validation successful");
    }

    /**
     * Generate a new JWT token for the given username.
     * Token includes username as subject, issued-at time, and expiration.
     * 
     * @param username The subject (username) for the token
     * @return Compact JWT token string
     */
    public String generateToken(@NonNull String username) {
        log.debug("Generating JWT token for user: {}", username);
        
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        
        log.info("JWT token generated for user: {}", username);
        return token;
    }

    /**
     * Extract username from JWT token claims.
     * 
     * @param token JWT token string
     * @return Username (subject) from token
     * @throws JwtException if token is invalid
     */
    public String extractUsername(@NonNull String token) {
        log.debug("Extracting username from JWT token");
        String username = extractClaims(token).getSubject();
        log.debug("Username extracted: {}", username);
        return username;
    }

    /**
     * Validate JWT token integrity and expiration.
     * 
     * @param token JWT token string
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean isTokenValid(@NonNull String token) {
        try {
            Claims claims = extractClaims(token);
            boolean isValid = claims.getSubject() != null && claims.getExpiration().after(new Date());
            
            if (isValid) {
                log.debug("JWT token validation successful");
            } else {
                log.warn("JWT token validation failed: token expired or invalid");
            }
            
            return isValid;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Parse and extract claims from JWT token.
     * Requires valid signature and non-expired token.
     * 
     * @param token JWT token string
     * @return Claims object containing token data
     * @throws JwtException if token is invalid or signature doesn't match
     */
    private Claims extractClaims(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get the signing key for HMAC SHA-256 algorithm.
     * 
     * @return Signing key derived from secret
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
