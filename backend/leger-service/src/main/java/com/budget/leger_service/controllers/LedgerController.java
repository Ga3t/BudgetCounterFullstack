package com.budget.leger_service.controllers;


import com.budget.leger_service.dto.*;
import com.budget.leger_service.services.LedgerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ledger/")
public class LedgerController {

    private LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/createtran")
    public ResponseEntity<String> createTransaction(@Valid @RequestBody CreateTransacDto createTransacDto,
                                                    @RequestHeader("X-User-ID") String userId){
        String ledgerResponse = ledgerService.createTransaction(createTransacDto, userId);
        return new ResponseEntity<>(ledgerResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getmonthstat/{year}/{month}")
    public ResponseEntity<MonthStatDto> getMonthStatistic(@PathVariable ("year") int year,
                                                          @PathVariable("month") int month,
                                                          @RequestHeader("X-User-ID") String userId){
        MonthStatDto monthStatDto = ledgerService.showMonthStats(year, month, userId);
        return new ResponseEntity<>(monthStatDto, HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<LedgerResponse> getListOfTransactions (@RequestParam(value="pageNo", defaultValue = "0", required = false) int pageNo,
                                                                 @RequestParam(value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                                 @RequestHeader ("X-User-ID") String userId,
                                                                 TransactionFilter transactionFilter){
        if(transactionFilter != null && isFilterNotEmpty(transactionFilter)){
            LedgerResponse response = ledgerService.findAllTransactionsWithCustomFilter(userId, transactionFilter, pageNo, pageSize);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            LedgerResponse response = ledgerService.findAllTransactions(userId, pageNo, pageSize);
            return new ResponseEntity<>(response, HttpStatus.OK);}

    }

    @PostMapping("/changetransaction")
    public ResponseEntity<String> changeTransaction(@RequestHeader("X-User-ID") String userId,
                                                    @RequestBody ChangeTranDto changetranDto){
        String response = ledgerService.changeTransaction(changetranDto, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private boolean isFilterNotEmpty(TransactionFilter filter) {
        return filter.startDate() != null ||
                filter.endDate() != null ||
                filter.startPrice() != null ||
                filter.endPrice() != null ||
                filter.CategoryName() != null ||
                filter.CategoryType() != null;
    }

}
