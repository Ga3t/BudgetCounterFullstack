package com.budget.investments_service.models.dto;



import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCryptoTransactionDto {

    private String cryptoId;
    private LocalDateTime dateTime;
    private BigDecimal amount;
}
