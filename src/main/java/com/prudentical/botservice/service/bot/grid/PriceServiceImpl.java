package com.prudentical.botservice.service.bot.grid;

import com.prudentical.botservice.dto.PriceRequestDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.service.ExchangeService;

public class PriceServiceImpl implements PriceService {

    private final ExchangeService exchange;
    private final Grider grider;

    public PriceServiceImpl(ExchangeService exchange,
            Grider grider) {
        this.exchange = exchange;
        this.grider = grider;
    }

    public PriceInfo getCurrentPriceInfo(BotContext context, TradeType type) {
        var exchangeId = context.bot().getExchangeId();
        var pairId = context.bot().getPairId();

        var priceRequest = PriceRequestDTO.builder()
                .funds(context.gridUnitFund())
                .tradeType(type)
                .build();

        var price = this.exchange.getPriceFor(exchangeId, pairId, priceRequest);
        var priceGrid = this.grider.getPriceGrid(context.gridPricePoints(), price);
        return new PriceInfo(priceGrid, price);
    }
}
