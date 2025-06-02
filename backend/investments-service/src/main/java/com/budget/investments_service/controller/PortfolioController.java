package com.budget.investments_service.controller;


import com.budget.investments_service.models.dto.CreateCryptoTransactionDto;
import com.budget.investments_service.models.dto.CryptoFromPortfolioDto;
import com.budget.investments_service.services.CryptocurrencyService;
import com.budget.investments_service.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private PortfolioService portfolioService;
    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService, CryptocurrencyService cryptocurrencyService) {
        this.portfolioService = portfolioService;
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @PostMapping("/buycrypto")
    public ResponseEntity<String> buyCrypto(@RequestBody CreateCryptoTransactionDto createCryptoTransaction,
                                               @RequestHeader("X-User-ID") String userId){
        String response = portfolioService.buyCryptoTransaction(createCryptoTransaction, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/cellcrypto")
    public ResponseEntity<String> cellCrypto(@RequestBody CreateCryptoTransactionDto createCryptoTransaction,
                                                    @RequestHeader("X-User-ID") String userId){
        String response = portfolioService.cellCryptoTransaction(createCryptoTransaction, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getportfolio")
    public ResponseEntity<List<CryptoFromPortfolioDto>> getPortfolio(@RequestHeader("X-User-ID") String userId){
        List<CryptoFromPortfolioDto> response = portfolioService.getUserPortfolio(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
