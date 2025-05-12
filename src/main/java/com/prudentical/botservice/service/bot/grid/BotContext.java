package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.model.SpotGridBot;

import lombok.Builder;
import lombok.With;

@With
@Builder
record BotContext(
        long userId,
        long accountId,
        SpotGridBot bot,
        List<PositionDTO> openPositions,
        Optional<Integer> previousGrid,
        BigDecimal gridUnitFund,
        Map<Integer, BigDecimal> gridPricePoints) {
}