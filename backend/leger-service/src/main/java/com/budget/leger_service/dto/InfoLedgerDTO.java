package com.budget.leger_service.dto;


import com.budget.leger_service.models.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InfoLedgerDTO {
    Long id;
    LocalDateTime transactionDate;
    String ShortName;
    String Description;
    BigDecimal price;
    String CategoryName;
    String CategoryType;
}
