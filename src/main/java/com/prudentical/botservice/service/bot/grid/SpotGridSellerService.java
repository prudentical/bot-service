package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;

import com.prudentical.botservice.dto.PositionDTO;

public interface SpotGridSellerService {

    BotContext tryToSell(BotContext context);
    
    void closePosition(BotContext context, BigDecimal price, PositionDTO openPosition);

}
