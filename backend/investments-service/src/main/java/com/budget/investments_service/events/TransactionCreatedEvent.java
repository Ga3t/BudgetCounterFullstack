package com.budget.investments_service.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCreatedEvent {

    private Long userId;
    private BigDecimal price;
    private String shortName;
    private LocalDateTime dateTime;
    private String categoryName;


    public TransactionCreatedEvent() {
    }

    public TransactionCreatedEvent(Long userId, BigDecimal price, String shortName, LocalDateTime dateTime, String categoryId) {
        this.userId = userId;
        this.price = price;
        this.shortName = shortName;
        this.dateTime = dateTime;
        this.categoryName = categoryId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
