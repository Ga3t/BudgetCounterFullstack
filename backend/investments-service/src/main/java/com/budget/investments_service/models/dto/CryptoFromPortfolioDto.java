package com.budget.investments_service.models.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CryptoFromPortfolioDto {

    private String cryptoName;
    private BigDecimal amount;
    private BigDecimal currentPrice;
    private LocalDateTime last_update;
}
