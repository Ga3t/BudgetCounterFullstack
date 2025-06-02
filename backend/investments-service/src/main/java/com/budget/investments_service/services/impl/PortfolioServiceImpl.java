package com.budget.investments_service.services.impl;


import com.budget.investments_service.events.TransactionCreatedEvent;
import com.budget.investments_service.exceptions.NotEnoughCryptoException;
import com.budget.investments_service.exceptions.TransactionIsBeforeLastUpdateException;
import com.budget.investments_service.models.CryptocurrencyEntity;
import com.budget.investments_service.models.PortfolioEntity;
import com.budget.investments_service.models.TransactionEntity;
import com.budget.investments_service.models.TransactionType;
import com.budget.investments_service.models.dto.CreateCryptoTransactionDto;
import com.budget.investments_service.models.dto.CryptoFromPortfolioDto;
import com.budget.investments_service.models.dto.DailyPriceDto;
import com.budget.investments_service.repository.CryptocurrencyRepository;
import com.budget.investments_service.repository.PortfolioRepository;
import com.budget.investments_service.repository.TransactionRepository;
import com.budget.investments_service.services.CryptocurrencyService;
import com.budget.investments_service.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Primary
public class PortfolioServiceImpl implements PortfolioService{

    private PortfolioRepository portfolioRepository;
    private CryptocurrencyService cryptocurrencyService;
    private CryptocurrencyRepository cryptocurrencyRepository;
    private TransactionRepository transactionRepository;
    private KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                CryptocurrencyService cryptocurrencyService,
                                CryptocurrencyRepository cryptocurrencyRepository,
                                TransactionRepository transactionRepository, KafkaTemplate<String,
                                TransactionCreatedEvent> kafkaTemplate) {
        this.portfolioRepository = portfolioRepository;
        this.cryptocurrencyService = cryptocurrencyService;
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public String buyCryptoTransaction(CreateCryptoTransactionDto createCryptoTransactionDTO, String userId) {

        Long userLongId= Long.parseLong(userId);
        TransactionEntity newTransaction = new TransactionEntity();
        String transactionId = UUID.randomUUID().toString();
        newTransaction.setUserId(userLongId);

        String cryptoId= createCryptoTransactionDTO.getCryptoId();
        BigDecimal cryptoAmount = createCryptoTransactionDTO.getAmount();
        LocalDateTime timeTransaction= createCryptoTransactionDTO.getDateTime();

        List<DailyPriceDto> dailyPriceDtos = cryptocurrencyService.getDailyCryptoPrice(cryptoId);
        BigDecimal priceCryptoForOne = cryptocurrencyService.findPriceAtTime24h(dailyPriceDtos,timeTransaction);

        if(Objects.equals(priceCryptoForOne, BigDecimal.ZERO)){
            LocalDateTime now = LocalDateTime.now();
            Duration maxAllowedOffset = Duration.ofHours(24);
            Duration actualOffset = Duration.between(timeTransaction, now).abs();

            if (actualOffset.compareTo(maxAllowedOffset) > 0) {
                priceCryptoForOne= cryptocurrencyService.findPriceAtTimeOld(cryptoId,timeTransaction);
                BigDecimal priceCrypto = priceCryptoForOne.multiply(cryptoAmount);
                newTransaction.setPrice(priceCrypto);
            }
        }else{
            BigDecimal priceCrypto = priceCryptoForOne.multiply(cryptoAmount);
            newTransaction.setPrice(priceCrypto);
        }

            newTransaction.setDateTime(timeTransaction);
            newTransaction.setAmount(cryptoAmount);
            newTransaction.setType(TransactionType.BUY);
            Optional<CryptocurrencyEntity> crypto = cryptocurrencyRepository.findByCryptoId(cryptoId);
        if(crypto.isEmpty()){
            CryptocurrencyEntity newCrypto = new CryptocurrencyEntity();
            newCrypto.setCryptoId(cryptoId);
            cryptocurrencyRepository.save(newCrypto);
            newTransaction.setCryptocurrency(newCrypto);
            addToPortfolio(newCrypto, cryptoAmount,userLongId);
        }else {
            CryptocurrencyEntity newCrypto = crypto.orElseThrow(()->new RuntimeException("Temporary solution"));
            newTransaction.setCryptocurrency(newCrypto);
            addToPortfolio(newCrypto, cryptoAmount,userLongId);
        }
        transactionRepository.save(newTransaction);
        String transactionDescription = String.format("Purchase of %s", cryptoId);
        TransactionCreatedEvent transactionCreatedEvent = new TransactionCreatedEvent(userLongId,
                newTransaction.getPrice(), transactionDescription,newTransaction.getDateTime(),
                "BUY PORTFOLIO");
        kafkaTemplate.send("transaction-created-events-topic", transactionId, transactionCreatedEvent);

        return "Transaction successfully recorded";
    }

    @Override
    @Transactional
    public String cellCryptoTransaction(CreateCryptoTransactionDto createCryptoTransactionDTO, String userId) {

        Long userLongId= Long.parseLong(userId);
        String transactionId = UUID.randomUUID().toString();
        TransactionEntity newTransaction = new TransactionEntity();
        String cryptoId = createCryptoTransactionDTO.getCryptoId();
        LocalDateTime transactionTime = createCryptoTransactionDTO.getDateTime();
        newTransaction.setUserId(userLongId);

        BigDecimal amount= createCryptoTransactionDTO.getAmount();
        Optional<CryptocurrencyEntity> crypto = cryptocurrencyRepository.findByCryptoId(cryptoId);

        CryptocurrencyEntity newCrypto = crypto.orElseGet(() -> {
            CryptocurrencyEntity newOne = new CryptocurrencyEntity();
            newOne.setCryptoId(cryptoId);
            cryptocurrencyRepository.save(newOne);
            return newOne;
        });

        boolean portfolioCheck = withdrawFromPortfolio(newCrypto, amount, userLongId, transactionTime);

        if (portfolioCheck) {
            newTransaction.setType(TransactionType.SELL);
            newTransaction.setAmount(amount);
            newTransaction.setCryptocurrency(newCrypto);
            newTransaction.setUserId(userLongId);
            newTransaction.setDateTime(transactionTime);

            List<DailyPriceDto> dailyPriceDtos = cryptocurrencyService.getDailyCryptoPrice(cryptoId);
            BigDecimal priceCryptoForOne = cryptocurrencyService.findPriceAtTime24h(dailyPriceDtos, transactionTime);

            if (priceCryptoForOne.equals(BigDecimal.ZERO)) {
                Duration maxAllowedOffset = Duration.ofHours(24);
                Duration actualOffset = Duration.between(transactionTime, LocalDateTime.now()).abs();

                if (actualOffset.compareTo(maxAllowedOffset) > 0) {
                    priceCryptoForOne = cryptocurrencyService.findPriceAtTimeOld(cryptoId, transactionTime);
                }
            }
            BigDecimal priceCrypto = priceCryptoForOne.multiply(amount);
            newTransaction.setPrice(priceCrypto);
            transactionRepository.save(newTransaction);
            String transactionDescription = String.format("Sell %s from portfolio", cryptoId);
            TransactionCreatedEvent transactionCreatedEvent = new TransactionCreatedEvent(userLongId,
                    priceCrypto, transactionDescription,transactionTime, "CELL PORTFOLIO");

            kafkaTemplate.send("transaction-created-events-topic", transactionId, transactionCreatedEvent);

            return "Transaction successfully recorded";
        }else {
            return "Failed to create transaction";
        }
    }


    @Override
    @Transactional
    public List<CryptoFromPortfolioDto> getUserPortfolio(String userId) {

        Long userLongId = Long.parseLong(userId);
        List<PortfolioEntity> portfolioEntities = portfolioRepository.findByUserId(userLongId);

        List<CryptoFromPortfolioDto> response = new ArrayList<>();
        for(int i=0; portfolioEntities.size()>i; i++ ){
            PortfolioEntity entity= portfolioEntities.get(i);
            String cryptoId = entity.getCryptocurrency().getCryptoId();
            CryptoFromPortfolioDto dto = new CryptoFromPortfolioDto();
            dto.setAmount(entity.getAmount());
            dto.setCryptoName(cryptoId);
            List<DailyPriceDto> dailyPriceList = cryptocurrencyService.getDailyCryptoPrice(cryptoId);
            BigDecimal priceForOne = cryptocurrencyService.findPriceAtTime24h(dailyPriceList, LocalDateTime.now(Clock.systemUTC()));
            BigDecimal price = priceForOne.multiply(entity.getAmount());
            dto.setCurrentPrice(price);
            dto.setLast_update(entity.getLast_update());
            response.add(dto);
        }

        return response;
    }

    @Override
    @Transactional
    public void addToPortfolio(CryptocurrencyEntity crypto, BigDecimal amount, Long userId){

        Optional<PortfolioEntity> optionalPortfolio = portfolioRepository.findByUserIdAndCryptocurrency(userId, crypto);

        PortfolioEntity portfolio;

        if (optionalPortfolio.isPresent()) {
            portfolio= optionalPortfolio.orElseThrow(()-> new RuntimeException("Server error"));
            portfolio.setLast_update(LocalDateTime.now());
            BigDecimal oldAmount = portfolio.getAmount();
            BigDecimal newAmount = oldAmount.add(amount);
            portfolio.setAmount(newAmount);
        } else {
            portfolio = new PortfolioEntity();
            portfolio.setLast_update(LocalDateTime.now());
            portfolio.setUserId(userId);
            portfolio.setCryptocurrency(crypto);
            portfolio.setAmount(amount);
        }
        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public boolean withdrawFromPortfolio(CryptocurrencyEntity crypto, BigDecimal amount, Long userId,LocalDateTime transactionTime){

        Optional<PortfolioEntity> optionalPortfolio = portfolioRepository.findByUserIdAndCryptocurrency(userId, crypto);
        LocalDateTime last_update = optionalPortfolio.get().getLast_update();
        PortfolioEntity portfolio= new PortfolioEntity();
        portfolio.setLast_update(LocalDateTime.now(Clock.systemUTC()));
        if(transactionTime.isBefore(last_update.minusHours(1)))
            throw new TransactionIsBeforeLastUpdateException("The validity of the transaction cannot be determined");
        if (optionalPortfolio.isPresent()) {
                portfolio = optionalPortfolio.get();
                BigDecimal oldAmount = portfolio.getAmount();
                String cryptoId = crypto.getCryptoId();
                if (oldAmount.equals(BigDecimal.ZERO))
                    throw new NotEnoughCryptoException("Not enough {cryptoId} on your balance");
                else {
                    if (oldAmount.compareTo(amount) < 0) {
                        throw new NotEnoughCryptoException("Not enough " + crypto.getCryptoId() + " on your balance");
                    }
                    portfolio.setAmount(oldAmount.subtract(amount));
                    portfolioRepository.save(portfolio);
                    return true;
                }

        }
        return false;
    }
}

