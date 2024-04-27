package com.prudentical.botservice.dto;

import java.math.BigDecimal;

public record AccountDTO(
        String name,
        long userId,
        long exchangeId,
        BigDecimal capital,
        BigDecimal lockedCapital) {

}
