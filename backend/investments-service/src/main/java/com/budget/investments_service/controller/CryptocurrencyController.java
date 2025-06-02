package com.budget.investments_service.controller;


import com.budget.investments_service.models.dto.CryptoDto;
import com.budget.investments_service.models.dto.DailyPriceDto;
import com.budget.investments_service.services.CryptocurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cryptocurrency")
public class CryptocurrencyController {

    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    public CryptocurrencyController(CryptocurrencyService cryptocurrencyService) {
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @GetMapping("/getlist")
    public ResponseEntity<Page<CryptoDto>> findListOfCryptocurrency(@RequestParam(value="pageNo", defaultValue = "0", required = false) int pageNo,
                                             @RequestParam(value="pageSize", defaultValue = "10", required = false) int pageSize){

        Page<CryptoDto> cryptoList = cryptocurrencyService.getListOfCryptocurrency(pageSize, pageNo);

        return new ResponseEntity<>(cryptoList, HttpStatus.OK);
    }

    @GetMapping("/dailyprice")
    public ResponseEntity<List<DailyPriceDto>>getDaily(@RequestParam(value = "cryptoId", defaultValue ="") String cryptoId){
        if(cryptoId==null)
            throw new IllegalArgumentException("No cryptocurrency selected");

        List<DailyPriceDto> dailyPriceDtos = cryptocurrencyService.getDailyCryptoPrice(cryptoId);
        return new ResponseEntity<>(dailyPriceDtos, HttpStatus.OK);
    }

}
