package com.budget.investments_service.models;


import jakarta.persistence.*;

@Entity
@Table(name= "CRYPTOCURRENCY")
public class CryptocurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name= "CRYPTO_ID", unique = true)
    private String cryptoId;


//    @OneToMany(mappedBy = "cryptocurrency", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PortfolioEntity> portfolios;
//
//    @OneToMany(mappedBy = "cryptocurrency", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TransactionEntity> transactions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(String cryptoId) {
        this.cryptoId = cryptoId;
    }
}
