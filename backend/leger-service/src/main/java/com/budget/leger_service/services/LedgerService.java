package com.budget.leger_service.services;


import com.budget.leger_service.dto.*;
import com.budget.leger_service.models.Category;
import com.budget.leger_service.models.LedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LedgerService {

    String createTransaction(CreateTransacDto createTransacDto, String token);
    String changeTransaction (ChangeTranDto changeTranDto, String userId);
    MonthStatDto showMonthStats(int year, int month, String userId);
    String deleteTransaction(Long ledgerId);
    LedgerResponse findAllTransactionsWithCustomFilter(String userId, TransactionFilter filter, int pageNo, int pageSize);
    LedgerResponse findAllTransactions(String UserId, int pageNo, int pageSize);
    List<Category> findAllCategories();
}
