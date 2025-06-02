package com.budget.leger_service.dto;

import com.budget.leger_service.models.CategoryType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategorySumDto {
    String categoryName;
    BigDecimal sum;
    CategoryType categoryType;

    public CategorySumDto(String categoryName, BigDecimal sum, CategoryType categoryType) {
        this.categoryName = categoryName;
        this.sum = sum;
        this.categoryType = categoryType;
    }
}

