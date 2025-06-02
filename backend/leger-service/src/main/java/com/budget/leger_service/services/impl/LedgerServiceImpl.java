package com.budget.leger_service.services.impl;

import com.budget.leger_service.dto.*;
import com.budget.leger_service.exceptions.DataStorageException;
import com.budget.leger_service.models.Category;
import com.budget.leger_service.models.LedgerEntity;
import com.budget.leger_service.repository.CategoryRepository;
import com.budget.leger_service.repository.LedgerRepository;
import com.budget.leger_service.services.LedgerService;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Primary
public class LedgerServiceImpl implements LedgerService{

    private LedgerRepository ledgerRepository;
    private CategoryRepository categoryRepository;
    private static final Logger log = LoggerFactory.getLogger(LedgerServiceImpl.class);

    public LedgerServiceImpl(LedgerRepository ledgerRepository, CategoryRepository categoryRepository) {
        this.ledgerRepository = ledgerRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public String createTransaction(CreateTransacDto createTransacDto, String userId) {

        LocalDateTime dateOfTran = createTransacDto.getTime();
//        LocalDateTime currentDateTime = LocalDateTime.now();
//
//        if(dateOfTran.isAfter(currentDateTime))
//            throw new CreateTransactionExceptions("Transaction cannot be in the future!");

        try{
            Optional<Category> category = categoryRepository.findByName(createTransacDto.getCategoryName());
            if(category.isEmpty())
                throw new NotFoundException("Category not found exception");
            LedgerEntity ledger = new LedgerEntity();
            ledger.setUserId(Long.parseLong(userId));
            ledger.setDate(createTransacDto.getTime());
            ledger.setShortName(createTransacDto.getName());
            ledger.setCategory(category.orElse(null));
            ledger.setPrice(createTransacDto.getPrice());
            ledger.setDate(dateOfTran);
            ledgerRepository.save(ledger);}
        catch(DataAccessException dataAccessException){
            log.error("Failed to save transaction for user {}: {}", userId, dataAccessException.getMessage(), dataAccessException);
            throw new DataStorageException("Failed to save your transaction. Please try again later.", dataAccessException);
        }
        return "Transactions successfully saved!";
    }

    @Override
    @Transactional
    public String changeTransaction(ChangeTranDto changeTranDto, String userId) {

        Optional<LedgerEntity> ledgerOptional = ledgerRepository.findById(changeTranDto.getId());
        String categoryNameToUpdate = changeTranDto.getCategory();
        if(ledgerOptional.isEmpty())
            throw new NotFoundException("Such transaction not found");

        LedgerEntity ledger = ledgerOptional.get();
        if(!ledger.getUserId().equals(Long.parseLong(userId)) ){
            log.warn("User {} attempted to modify transaction {} owned by another user",
                    userId, ledger.getId());
            throw new ForbiddenException("This transaction does not belong to this user");
        }

        if(changeTranDto.getPrice() != null)
            ledger.setPrice(changeTranDto.getPrice());
        if(changeTranDto.getDate() != null)
            ledger.setDate(changeTranDto.getDate());
        if(changeTranDto.getDescription() != null)
            ledger.setDescription(changeTranDto.getDescription());
        if (categoryNameToUpdate != null) {
            if (ledger.getCategory() == null || !ledger.getCategory().getName().equals(categoryNameToUpdate)) {
                Optional<Category> categoryOptional = categoryRepository.findByName(categoryNameToUpdate);
                if (categoryOptional.isEmpty()) {
                    log.warn("Category '{}' not found during transaction update for ID: {}",
                            categoryNameToUpdate, ledger.getId());
                    throw new NotFoundException("Category with name '" + categoryNameToUpdate + "' not found.");
                }
                ledger.setCategory(categoryOptional.get());
            }
        }
        if(changeTranDto.getShortName() != null)
            ledger.setShortName(changeTranDto.getShortName());
        try{
            ledgerRepository.save(ledger);
        }catch(DataAccessException dataAccessException){
            log.error("Failed to change transaction ");
            throw new DataStorageException("Failed to change your transaction. Please try again later.", dataAccessException);
        }

        return "Transaction changed successfully";
    }

    @Override
    public MonthStatDto showMonthStats(int year, int month, String userId) {
        Long userIdLong = Long.parseLong(userId);
        MonthStatDto monthStatDto = new MonthStatDto();
        BigDecimal biggestIncome = ledgerRepository.findBiggestIncome(userIdLong, year, month).orElse(BigDecimal.ZERO);
        BigDecimal biggestExpense = ledgerRepository.findBiggestExpense(userIdLong, year, month).orElse(BigDecimal.ZERO);
        BigDecimal totalIncomeForMonth = ledgerRepository.sumIncomeForMonth(userIdLong, year, month).orElse(BigDecimal.ZERO);
        BigDecimal totalExpensesForMonth =ledgerRepository.sumExpenseForMonth(userIdLong, year, month).orElse(BigDecimal.ZERO);

        monthStatDto.setBiggestIncome(biggestIncome);
        monthStatDto.setBiggestExpenses(biggestExpense);
        monthStatDto.setTotalIncomeForMonth(totalIncomeForMonth);
        monthStatDto.setTotalExpensesForMonth(totalExpensesForMonth);
        BigDecimal difference= biggestIncome.subtract(biggestExpense);

        monthStatDto.setDifference(difference);

        BigDecimal previousTotalIncome;
        BigDecimal previousTotalExpenses;
        if (month == 1) {
            previousTotalIncome = ledgerRepository.sumIncomeForMonth(userIdLong, year - 1, 12)
                    .orElse(BigDecimal.ZERO);
            previousTotalExpenses = ledgerRepository.sumExpenseForMonth(userIdLong, year - 1, 12)
                    .orElse(BigDecimal.ZERO);
        } else {
            previousTotalIncome = ledgerRepository.sumIncomeForMonth(userIdLong, year, month - 1)
                    .orElse(BigDecimal.ZERO);
            previousTotalExpenses = ledgerRepository.sumExpenseForMonth(userIdLong, year, month - 1)
                    .orElse(BigDecimal.ZERO);
        }

        BigDecimal incomeChange;
        if (previousTotalIncome.compareTo(BigDecimal.ZERO) == 0) {
            if (totalIncomeForMonth.compareTo(BigDecimal.ZERO) > 0) {
                incomeChange = new BigDecimal("100");
            } else {
                incomeChange = BigDecimal.ZERO;
            }
        } else {
            incomeChange = totalIncomeForMonth.subtract(previousTotalIncome)
                    .divide(previousTotalIncome, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        monthStatDto.setIncomeChange(incomeChange);


        BigDecimal expensesChange;
        if (previousTotalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            if (totalExpensesForMonth.compareTo(BigDecimal.ZERO) > 0) {
                expensesChange = new BigDecimal("100");
            } else {
                expensesChange = BigDecimal.ZERO;
            }
        } else {
            expensesChange = totalExpensesForMonth.subtract(previousTotalExpenses)
                    .divide(previousTotalExpenses, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        monthStatDto.setExpensesChange(expensesChange);
        List<CategorySumDto> categoryData = ledgerRepository.findCategorySumsForMonth(userIdLong, year, month);
        monthStatDto.setCategoryCircle(categoryData);
        return monthStatDto;
    }

    @Override
    public String deleteTransaction(Long ledgerId) {
       return null;
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerResponse findAllTransactionsWithCustomFilter(String userId, TransactionFilter filter,int pageNo, int pageSize) {


        Pageable pageable =PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.by("id")
                )
        );
        try{
            Page<LedgerEntity> ledgerPages = ledgerRepository.findWithCustomFilter(Long.parseLong(userId), pageable, filter);

            if(ledgerPages.isEmpty()){
                pageable = PageRequest.of(
                        0,
                        pageSize,
                        Sort.by(
                                Sort.Order.desc("date"),
                                Sort.Order.by("id")
                        )
                );
                ledgerPages= ledgerRepository.findWithCustomFilter(Long.parseLong(userId), pageable, filter);
            }
            List<InfoLedgerDTO> dtoList = ledgerPages.getContent().stream()
                    .map(this::convertToInfoLedgerDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            LedgerResponse response = new LedgerResponse();
            response.setInfoLedgerDTO(dtoList);
            response.setPageNo(pageNo);
            response.setPageSize(pageSize);
            response.setLast(ledgerPages.isLast());
            response.setTotalPages(ledgerPages.getTotalPages());
            response.setTotalElements(ledgerPages.getTotalElements());
            
            return response;
        }catch(DataAccessException ex){
            log.error("Database currently unavailable");
            throw new NotFoundException("Some problems with db!", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerResponse findAllTransactions(String userId, int pageNo, int pageSize) {


        Pageable pageable = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.by("id")
                ));
        try {
            Page<LedgerEntity> ledgerPages = ledgerRepository.findByUserId(Long.parseLong(userId), pageable);

            List<InfoLedgerDTO> dtoList = ledgerPages.getContent().stream()
                    .map(this::convertToInfoLedgerDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            LedgerResponse response = new LedgerResponse();
            response.setInfoLedgerDTO(dtoList);
            response.setPageNo(pageNo);
            response.setPageSize(pageSize);
            response.setLast(ledgerPages.isLast());
            response.setTotalPages(ledgerPages.getTotalPages());
            response.setTotalElements(ledgerPages.getTotalElements());
            return response;
        }catch (DataAccessException ex){
            log.error("Database currently unavailable", ex);
            throw new DataStorageException("Database currently unavailable");
        }

    }

    @Override
    public List<Category> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

    private InfoLedgerDTO convertToInfoLedgerDto(LedgerEntity ledger){
        if(ledger == null ) return null;

        if (ledger.getCategory() == null) {
            log.error("CRITICAL DATA INCONSISTENCY: Transaction_id {} for user_id {} has NULL category! Skipping this transaction from the list.",
                    ledger.getId(), ledger.getUserId());
            return null;
        }

        InfoLedgerDTO infoLedgerDTO = new InfoLedgerDTO();
        infoLedgerDTO.setId(ledger.getId());
        infoLedgerDTO.setTransactionDate(ledger.getDate());
        infoLedgerDTO.setShortName(ledger.getShortName());
        infoLedgerDTO.setDescription(ledger.getDescription());
        infoLedgerDTO.setPrice(ledger.getPrice());
        infoLedgerDTO.setCategoryName(ledger.getCategory().getName());
        infoLedgerDTO.setCategoryType(ledger.getCategory().getCategoryType().toString());
        return infoLedgerDTO;
    }

}
