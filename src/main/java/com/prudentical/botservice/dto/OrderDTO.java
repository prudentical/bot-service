package com.prudentical.botservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record OrderDTO(
        long id,
        String internalId,
        BigDecimal amount,
        BigDecimal price,
        TradeType type,
        OrderStatus status,
        Instant timestamp) {

    public boolean isBuyOrder(){
        return type == TradeType.Buy;
    }
    
    public boolean isSellOrder(){
        return type == TradeType.Buy;
    }
}
