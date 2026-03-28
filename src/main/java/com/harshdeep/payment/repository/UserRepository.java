package com.harshdeep.payment.repository;

import com.harshdeep.payment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity persistence operations.
 * 
 * Extends JpaRepository to provide CRUD operations plus custom query methods
 * for user authentication (lookup by username).
 * 
 * All methods are transactional by default through Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their unique username.
     * 
     * Used for authentication during login and JWT token generation.
     * The username is case-sensitive.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, or empty Optional if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username already exists.
     * 
     * Used during user registration to prevent duplicate usernames.
     * 
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
