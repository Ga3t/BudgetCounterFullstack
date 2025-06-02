package com.budget.investments_service.repository;


import com.budget.investments_service.models.CryptocurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptocurrencyRepository extends JpaRepository<CryptocurrencyEntity,Long> {

    Optional<CryptocurrencyEntity> findByCryptoId(String cryptoId);

}
