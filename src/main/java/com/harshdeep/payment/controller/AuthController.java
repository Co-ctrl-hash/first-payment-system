package com.harshdeep.payment.controller;

import com.harshdeep.payment.entity.User;
import com.harshdeep.payment.repository.UserRepository;
import com.harshdeep.payment.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST Controller for user authentication endpoints.
 * Handles user registration and login with JWT token generation.
 * 
 * All password are securely hashed using BCrypt before persistence.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with username and password.
     * 
     * @param user User object containing username and password
     * @return Created user entity with hashed password
     * @throws ResponseStatusException if user data is invalid or username already exists
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        log.info("Registering new user: {}", user.getUsername());
        
        if (!isValidUsername(user.getUsername())) {
            log.warn("Invalid username provided for registration");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required and must not be blank");
        }

        if (!isValidPassword(user.getPassword())) {
            log.warn("Invalid password provided for registration");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required and must not be blank");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Registration failed: Username '{}' already exists", user.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Encrypt password using BCrypt before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Authenticate user and generate JWT token.
     * 
     * @param credentials User credentials (username and password)
     * @return JWT token string
     * @throws ResponseStatusException if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody User credentials) {
        log.info("Login attempt for user: {}", credentials.getUsername());
        
        if (!isValidUsername(credentials.getUsername()) || !isValidPassword(credentials.getPassword())) {
            log.warn("Login failed: Invalid credentials provided for user '{}'", credentials.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User dbUser = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User '{}' not found", credentials.getUsername());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });

        if (!passwordEncoder.matches(credentials.getPassword(), dbUser.getPassword())) {
            log.warn("Login failed: Password mismatch for user '{}'", credentials.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(credentials.getUsername());
        log.info("User logged in successfully: {} (Token generated)", dbUser.getUsername());
        
        return ResponseEntity.ok(token);
    }

    /**
     * Validates username is not null or blank.
     */
    private boolean isValidUsername(@NonNull String username) {
        return username != null && !username.isBlank();
    }

    /**
     * Validates password is not null or blank.
     */
    private boolean isValidPassword(@NonNull String password) {
        return password != null && !password.isBlank();
    }
}
