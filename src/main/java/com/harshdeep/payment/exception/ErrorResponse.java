package com.harshdeep.payment.exception;

import lombok.*;

/**
 * DTO for error response structure.
 * 
 * @author Harsh Deep
 * @version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private long timestamp;
}
