package com.budget.leger_service.dto;


import com.budget.leger_service.models.Category;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChangeTranDto {

    private Long id;

    private BigDecimal price;

    private String description;

    private String shortName;

    @PastOrPresent
    private LocalDateTime date;

    private String category;
}
