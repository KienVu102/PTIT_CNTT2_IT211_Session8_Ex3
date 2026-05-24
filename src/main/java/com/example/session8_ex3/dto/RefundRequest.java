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
