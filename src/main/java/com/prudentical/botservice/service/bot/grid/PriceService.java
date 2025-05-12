package com.prudentical.botservice.service.bot.grid;

import com.prudentical.botservice.dto.TradeType;

public interface PriceService {
    PriceInfo getCurrentPriceInfo(BotContext context, TradeType type);
}
