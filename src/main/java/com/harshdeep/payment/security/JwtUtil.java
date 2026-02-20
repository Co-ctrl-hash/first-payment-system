package com.harshdeep.payment.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    /**
     * Generate JWT token for authenticated user.
     * Token is valid for 24 hours.
     * 
     * @param username the username
     * @return JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    /**
     * Extract username from JWT token.
     * 
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
