package com.budget.leger_service.repository;

import com.budget.leger_service.dto.TransactionFilter;
import com.budget.leger_service.models.LedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterLedgerRepostory {
    Page<LedgerEntity> findWithCustomFilter(Long userId, Pageable pageable, TransactionFilter transactionFilter);
}
