package com.budget.investments_service.services.impl;


import com.budget.investments_service.tools.deserializer.OldPriceDeserializer;
import com.budget.investments_service.tools.deserializer.PriceListDeserializer;
import com.budget.investments_service.models.dto.CryptoDto;
import com.budget.investments_service.models.dto.DailyPriceDto;
import com.budget.investments_service.services.CryptocurrencyService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Primary
public class CryptocurrencyServiceImpl implements CryptocurrencyService {

    @Value("${api.coingecko.url}")
    private String apiUrl;

    @Value("${app.coingeko-api-key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private PriceListDeserializer priceListDeserializer;
    private OldPriceDeserializer oldPriceDeserializer;

    @Autowired
    public CryptocurrencyServiceImpl(RestTemplate restTemplate, PriceListDeserializer priceListDeserializer, OldPriceDeserializer oldPriceDeserializer) {
        this.restTemplate = restTemplate;
        this.priceListDeserializer = priceListDeserializer;
        this.oldPriceDeserializer = oldPriceDeserializer;
    }


    @Override
    public BigDecimal findPriceAtTime24h(List<DailyPriceDto> cachedPrices, LocalDateTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration maxAllowedOffset = Duration.ofHours(24);
        Duration actualOffset = Duration.between(targetTime, now).abs();

        if (actualOffset.compareTo(maxAllowedOffset) > 0) {
            return BigDecimal.ZERO;
        }

        Duration tolerance = Duration.ofMinutes(30);

        return cachedPrices.stream()
                .filter(p -> Duration.between(p.getDateTime(), targetTime).abs().compareTo(tolerance) <= 0)
                .sorted(Comparator.comparing(p -> Duration.between(p.getDateTime(), targetTime).abs()))
                .map(DailyPriceDto::getPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findPriceAtTimeOld(String cryptoId, LocalDateTime targetTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = targetTime.format(formatter);

        String url = apiUrl + "coins/{cryptoId}/history?date={formattedDate}&localization=false";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cryptoId", cryptoId);
        uriVariables.put("formattedDate", formattedDate);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class,
                uriVariables
        );

        BigDecimal price= BigDecimal.ZERO;

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonParser parser = new ObjectMapper().createParser(response.getBody());
                price = new OldPriceDeserializer().deserialize(parser, null);
                return price;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new RuntimeException();
        }
        return price;
    }


    @Override
    @Cacheable(value = "cryptoList", key = "#cryptoId")
    public List<CryptoDto> findFullCryptoList() {
        String url = apiUrl + "coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&include_platform=true";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<CryptoDto>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity, new ParameterizedTypeReference<List<CryptoDto>>() {});

        return response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    @Cacheable(value = "dailyCryptoList", key = "#cryptoId")
    public List<DailyPriceDto> findDailyPriceList(String cryptoId) {

        String url = apiUrl + "coins/{cryptoId}/market_chart?vs_currency=usd&days=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cryptoId", cryptoId);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class,
                uriVariables
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonParser parser = new ObjectMapper().createParser(response.getBody());
                return new PriceListDeserializer().deserialize(parser, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    @Override
    @Cacheable(value = "CryptoPriceNow", key = "#cryptoId")
    public DailyPriceDto findCryptoPriceNow(String cryptoId){
        String url = apiUrl + "simple/price"
                + "?ids=" + cryptoId
                + "&vs_currencies=usd"
                + "&include_last_updated_at=true";
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-demo-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);



        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                JsonNode cryptoNode = root.path(cryptoId.toLowerCase());
                if (!cryptoNode.isMissingNode()) {
                    BigDecimal price = cryptoNode.path("usd").decimalValue();
                    long timestamp = cryptoNode.path("last_updated_at").asLong();
                    LocalDateTime dateTime = Instant.ofEpochSecond(timestamp)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime();

                    return new DailyPriceDto(price, dateTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Page<CryptoDto> getListOfCryptocurrency(int pageSize, int pageNo) {
        List<CryptoDto> cryptoList = findFullCryptoList();
        int total = cryptoList.size();
        int fromIndex = Math.min(pageNo * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CryptoDto> pageContent = cryptoList.subList(fromIndex, toIndex);
        return new PageImpl<>(pageContent, PageRequest.of(pageNo, pageSize), total);
    }

    @Override
    public List<DailyPriceDto> getDailyCryptoPrice(String cryptoId){

        List<DailyPriceDto> dailyPrice = new ArrayList<>();
        try{
            dailyPrice= findDailyPriceList(cryptoId);
            dailyPrice.add(findCryptoPriceNow(cryptoId));
            return dailyPrice;
        }catch (Exception e){
            e.printStackTrace();
            if (!dailyPrice.isEmpty())
                return dailyPrice;
        }

        return dailyPrice;
    }


}
