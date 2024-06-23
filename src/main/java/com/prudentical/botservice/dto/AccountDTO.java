package com.prudentical.botservice.dto;

import java.math.BigDecimal;

import lombok.With;

@With
public record AccountDTO(
        long id,
        String name,
        long userId,
        long exchangeId,
        BigDecimal capital,
        BigDecimal lockedCapital) {

}
