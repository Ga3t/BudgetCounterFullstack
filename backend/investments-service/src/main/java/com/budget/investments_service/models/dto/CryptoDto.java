package com.budget.investments_service.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CryptoDto {
    @JsonProperty("id")
    String id;

    @JsonProperty("symbol")
    String symbol;

    @JsonProperty("include_platform")
    List<String> platforms;

    @JsonProperty("name")
    String name;

    @JsonProperty("current_price")
    BigDecimal currentPrice;

    @JsonProperty("last_updated")
    LocalDateTime lastUpdate;

    @JsonProperty("price_change_24h")
    BigDecimal priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    BigDecimal priceChangePercentage24h;

    @JsonProperty("image")
    String image;
}
