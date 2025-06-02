package com.budget.investments_service.models;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="TRANSACTIONS")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRYPTO_ID", nullable = false)
    private CryptocurrencyEntity cryptocurrency;

    @Column(name="PRICE_USD", nullable = false)
    private BigDecimal price;

    @Column(name="DATE", nullable = false)
    private LocalDateTime dateTime;

    @Column(name="CRYPTO_AMOUNT", nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name="TYPE", nullable = false)
    private TransactionType type;

    @Column(name="USER_ID", nullable = false)
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CryptocurrencyEntity getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(CryptocurrencyEntity cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
