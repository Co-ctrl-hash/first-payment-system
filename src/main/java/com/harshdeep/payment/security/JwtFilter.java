package com.harshdeep.payment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter for Spring Security.
 * InterceptsallHTTP requests and extracts JWT tokens from Authorization header.
 * Validates tokens and sets authentication context if valid.
 * 
 * Executes once per request (OncePerRequestFilter) to ensure single processing.
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = 7; // "Bearer ".length()

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filter method that intercepts HTTP requests and extracts JWT token from Authorization header.
     * If token is valid, sets authentication in SecurityContext.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                String token = authorizationHeader.substring(BEARER_LENGTH);
                log.debug("JWT token extracted from request");

                if (isValidTokenAndNoExistingAuth(token)) {
                    String username = jwtUtil.extractUsername(token);
                    log.info("JWT token valid for user: {}", username);
                    
                    setAuthenticationContext(username, request);
                } else {
                    log.warn("JWT token validation failed or authentication already exists");
                }
            }
        } catch (Exception ex) {
            log.error("Error processing JWT token in filter", ex);
            // Continue filter chain even if token processing fails
            // Response will be unauthorized by Spring Security's authorization filter
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Validate token and check that no authentication already exists.
     * 
     * @param token JWT token to validate
     * @return true if token is valid and no auth exists, false otherwise
     */
    private boolean isValidTokenAndNoExistingAuth(String token) {
        return jwtUtil.isTokenValid(token) 
                && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    /**
     * Set authentication context with extracted username.
     * 
     * @param username Username from token
     * @param request HTTP request for building authentication details
     */
    private void setAuthenticationContext(String username, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.emptyList() // No authorities/roles in this implementation
                );
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        log.debug("Authentication context set for user: {}", username);
    }
}
