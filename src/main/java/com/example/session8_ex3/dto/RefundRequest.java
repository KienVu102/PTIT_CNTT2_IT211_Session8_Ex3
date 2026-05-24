package com.example.session8_ex3.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    /**
     * Transaction code must match the pattern: alphanumeric characters only (e.g., TXN999).
     * This @Pattern regex rejects SQL injection payloads like "TXN999' OR '1'='1"
     * and XSS payloads containing special characters (<, >, ", ', ;, --, etc.).
     */
    @NotNull(message = "Transaction code must not be null")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Transaction code contains invalid characters. Only alphanumeric characters are allowed."
    )
    private String transactionCode;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    private Double amount;
}
