package com.budget.investments_service.repository;


import com.budget.investments_service.models.CryptocurrencyEntity;
import com.budget.investments_service.models.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    Optional<PortfolioEntity> findByUserIdAndCryptocurrency(Long userId,CryptocurrencyEntity crypto);
    List<PortfolioEntity>findByUserId(Long userId);
}
