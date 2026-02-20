package com.harshdeep.payment.controller;

import com.harshdeep.payment.entity.User;
import com.harshdeep.payment.repository.UserRepository;
import com.harshdeep.payment.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user authentication.
 * Provides endpoints for user registration and login with JWT token generation.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    /**
     * Constructor injection for dependencies.
     * 
     * @param userRepository the user repository
     * @param jwtUtil JWT utility for token generation
     * @param encoder BCrypt password encoder
     */
    public AuthController(UserRepository userRepository,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    /**
     * Register a new user with encrypted password.
     * 
     * @param user the user to register
     * @return the registered user
     */
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param user the user credentials
     * @return JWT token for authenticated user
     * @throws RuntimeException if user not found or credentials invalid
     */
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}
