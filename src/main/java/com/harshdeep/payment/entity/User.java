package com.harshdeep.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User entity representing a registered user in the payment system.
 * 
 * Stores basic user information including credentials.
 * Passwords are stored in BCrypt-hashed format for security.
 * 
 * Usernames must be unique and are used for authentication.
 */
@Entity
@Table(name = "users", indexes = @Index(name = "idx_username", columnList = "username"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for authentication (required).
     * Used as the principal in JWT claims.
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username can only contain alphanumeric characters, dots, underscores, and hyphens"
    )
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * BCrypt-hashed password for the user (required).
     * 
     * IMPORTANT: This field should contain ONLY hashed passwords.
     * Never store plain-text passwords.
     * Hash strength used: BCrypt with strength 10.
     */
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 60, max = 60, message = "Password hash must be exactly 60 characters (BCrypt hash format)")
    @Column(nullable = false)
    private String password;

    /**
     * Timestamp when this user account was created.
     * Automatically set by Hibernate at insert time.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
