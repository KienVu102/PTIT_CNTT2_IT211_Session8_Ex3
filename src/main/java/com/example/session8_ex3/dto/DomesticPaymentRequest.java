package com.example.session8_ex3.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomesticPaymentRequest {

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Currency must not be null")
    private String currency;
}
