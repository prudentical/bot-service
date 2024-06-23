package com.prudentical.botservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;

@Builder
public record PriceRequestDTO(
        String apiKey,
        BigDecimal amount,
        BigDecimal funds,
        Instant datetime,
        TradeType tradeType) {
}