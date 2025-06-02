package com.budget.investments_service.services;

import com.budget.investments_service.models.dto.DailyPriceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceTools {

    List<DailyPriceDto> parseResponseCryptoDailyPrice(String json);
}
