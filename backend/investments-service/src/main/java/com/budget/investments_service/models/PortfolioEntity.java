package com.budget.investments_service.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="PORTFOLIO")
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRYPTO_ID", nullable = false)
    private CryptocurrencyEntity cryptocurrency;

    @Column(name="AMOUNT", nullable = false, precision = 38, scale = 18)
    private BigDecimal amount;

    @Column(name="USER_ID", nullable = false)
    private Long userId;

    @Column(name="LAST_UPDATE", nullable = false)
    private LocalDateTime last_update;

    public LocalDateTime getLast_update() {
        return last_update;
    }

    public void setLast_update(LocalDateTime last_update) {
        this.last_update = last_update;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
