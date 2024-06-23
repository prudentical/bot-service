package com.prudentical.botservice.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.prudentical.botservice.dto.OrderRequestDTO;
import com.prudentical.botservice.dto.PriceRequestDTO;
import com.prudentical.botservice.dto.PriceResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final String endpoint = "http://exchange-service/exchanges/{exchangeId}/pairs/{pairId}";

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("null")
    public BigDecimal getPriceFor(long exchangeId, long pairId, PriceRequestDTO req) {
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .path("/price")
                .queryParamIfPresent("amount", Optional.ofNullable(req.amount()))
                .queryParamIfPresent("funds", Optional.ofNullable(req.funds()))
                .queryParamIfPresent("datetime", Optional.ofNullable(req.datetime()))
                .queryParamIfPresent("apiKey", Optional.ofNullable(req.apiKey()))
                .queryParam("tradeType", req.tradeType())
                .buildAndExpand(exchangeId, pairId)
                .toUri();
        log.debug("Getting price url [{}]", uri);
        var response = this.restTemplate.getForObject(uri, PriceResponseDTO.class);
        return response.price();
    }

    @Override
    public void order(long exchangeId, long pairId, OrderRequestDTO req) {
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .path("/order")
                .buildAndExpand(exchangeId, pairId)
                .toUri();
        this.restTemplate.postForLocation(uri, req);
    }

}
