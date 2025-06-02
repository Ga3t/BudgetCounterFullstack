package com.budget.leger_service.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MonthStatDto {
    BigDecimal totalIncomeForMonth;
    BigDecimal totalExpensesForMonth;
    BigDecimal biggestIncome;
    BigDecimal biggestExpenses;
    BigDecimal difference;
    BigDecimal incomeChange;
    BigDecimal expensesChange;
    List<CategorySumDto> categoryCircle;
}