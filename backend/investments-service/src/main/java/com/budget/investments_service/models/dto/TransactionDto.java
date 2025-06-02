package com.budget.investments_service.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionDto {
    @NotNull
    String name;
    @PastOrPresent
    @NotNull
    LocalDateTime dateTime;
    @Positive
    @NotNull
    BigDecimal quantity;
    @Positive
    @NotNull
    BigDecimal priceAtPurchase;
}
