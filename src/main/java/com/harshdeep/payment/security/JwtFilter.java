package com.harshdeep.payment.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter to validate and process JWT tokens.
 * Executes once per request before the authentication filter.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor injection for JwtUtil.
     * 
     * @param jwtUtil JWT utility
     */
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filter to extract and validate JWT token from Authorization header.
     * Sets authentication in SecurityContext if token is valid.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Invalid token - authentication will fail
            }
        }

        filterChain.doFilter(request, response);
    }
}
