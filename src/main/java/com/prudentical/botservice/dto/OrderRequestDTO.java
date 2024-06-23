package com.prudentical.botservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;

@Builder
public record OrderRequestDTO(
        long botId,
        String apiKey,
        BigDecimal amount,
        BigDecimal price,
        Boolean virtual,
        Instant datetime,
        TradeType type) {

}
