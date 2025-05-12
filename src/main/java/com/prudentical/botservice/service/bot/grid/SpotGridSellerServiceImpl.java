package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.OrderRequestDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.service.ExchangeService;

@Service
public class SpotGridSellerServiceImpl implements SpotGridSellerService {

    private final ExchangeService exchange;

    private final Grider grider;

    private final PriceService price;

    @Autowired
    public SpotGridSellerServiceImpl(ExchangeService exchange,
            Grider grider,
            PriceService price) {
        this.exchange = exchange;
        this.grider = grider;
        this.price = price;
    }

    public BotContext tryToSell(BotContext context) {
        var priceInfo = this.price.getCurrentPriceInfo(context, TradeType.Sell);

        Predicate<PositionDTO> inProfit = position -> priceInfo.priceGrid()
                .filter(grid -> grid <= this.grider.getPositionGrid(context.gridPricePoints(), position) + 2)
                .isPresent();

        context.openPositions().stream()
                .filter(inProfit)
                .findFirst()
                .ifPresent(position -> closePosition(context, priceInfo.price(), position));

        return context;
    }

    public void closePosition(BotContext context, BigDecimal price, PositionDTO openPosition) {
        var amount = openPosition.orders().stream()
                .filter(OrderDTO::isBuyOrder)
                .map(OrderDTO::amount).reduce(BigDecimal::add).get();
        var orderRequest = OrderRequestDTO.builder()
                .botId(context.bot().getId())
                .amount(amount)
                .price(price)
                .type(TradeType.Sell)
                .build();
        this.exchange.order(context.bot().getExchangeId(), context.bot().getPairId(), orderRequest);

        var openPositions = context.bot().getOpenPositionGrids().stream()
                .filter(i -> i != this.grider.getPositionGrid(context.gridPricePoints(), openPosition))
                .toList();
        context.bot().setOpenPositionGrids(openPositions);
    }

}
