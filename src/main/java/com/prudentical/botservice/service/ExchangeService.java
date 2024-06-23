package com.prudentical.botservice.service;

import java.math.BigDecimal;

import com.prudentical.botservice.dto.OrderRequestDTO;
import com.prudentical.botservice.dto.PriceRequestDTO;

public interface ExchangeService {

    BigDecimal getPriceFor(long exchangeId, long pairId, PriceRequestDTO req);
    
    void order(long exchangeId, long pairId, OrderRequestDTO req);
}
