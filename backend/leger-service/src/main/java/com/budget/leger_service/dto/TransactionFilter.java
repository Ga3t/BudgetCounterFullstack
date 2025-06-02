package com.budget.leger_service.dto;

import com.budget.leger_service.models.CategoryType;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        BigDecimal startPrice,
        BigDecimal endPrice,
        String CategoryName,
        CategoryType CategoryType)
{}
