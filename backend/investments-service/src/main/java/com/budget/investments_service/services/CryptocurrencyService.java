package com.budget.investments_service.services;

import com.budget.investments_service.models.dto.CryptoDto;
import com.budget.investments_service.models.dto.DailyPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface CryptocurrencyService {

    List<DailyPriceDto> getDailyCryptoPrice(String cryptoId);
    Page<CryptoDto> getListOfCryptocurrency(int pageSize, int pageNo);
    List<CryptoDto> findFullCryptoList();
    List<DailyPriceDto> findDailyPriceList(String cryptoId);
    DailyPriceDto findCryptoPriceNow(String cryptoId);
    BigDecimal findPriceAtTime24h(List<DailyPriceDto> cachedPrices, LocalDateTime targetTime);
    BigDecimal findPriceAtTimeOld(String cryptoId, LocalDateTime targetTime);
}
