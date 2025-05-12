package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.dto.OrderRequestDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.service.ExchangeService;

@Service
public class SpotGridBuyerServiceImpl implements SpotGridBuyerService {

    private final ExchangeService exchange;

    private final Grider grider;

    private final PriceService price;

    @Autowired
    public SpotGridBuyerServiceImpl(ExchangeService exchange,
            Grider grider,
            PriceService price) {
        this.exchange = exchange;
        this.grider = grider;
        this.price = price;
    }

    @Override
    public BotContext tryToBuy(BotContext context) {
        var currentPrice = this.price.getCurrentPriceInfo(context, TradeType.Sell);
        var currentGrid = currentPrice.priceGrid();
        var openPositionGrids = getOpenPositionGrids(context);

        var hasNoOpenPositionAtThisGrid = currentGrid
                .filter(grid -> !openPositionGrids.contains(grid))
                .isPresent();

        var hasPriceGoneToLowerGrids = context.previousGrid()
                .filter(prevGrid -> prevGrid > 1)
                .filter(prevGrid -> currentGrid.filter(grid -> grid < prevGrid && grid > 0).isPresent())
                .isPresent();

        var timeToBuy = hasNoOpenPositionAtThisGrid && hasPriceGoneToLowerGrids;

        if (timeToBuy) {
            var funds = context.bot().getCapital()
                    .divide(BigDecimal.valueOf(context.bot().getGrids()), 30, RoundingMode.HALF_UP);
            var orderRequest = OrderRequestDTO.builder()
                    .botId(context.bot().getId())
                    .amount(funds.divide(currentPrice.price(), 30, RoundingMode.HALF_UP))
                    .price(currentPrice.price())
                    .build();
            this.exchange.order(context.bot().getExchangeId(), context.bot().getPairId(), orderRequest);
            context.bot().getOpenPositionGrids().add(currentGrid.get());
        }
        context = context.withPreviousGrid(currentGrid);
        return context;
    }

    private List<Integer> getOpenPositionGrids(BotContext context) {
        var gridPricePoints = context.gridPricePoints();
        var openPositionGrids = context.bot().getOpenPositionGrids().stream();
        var openPositions = context.openPositions().stream()
                .map(position -> this.grider.getPositionGrid(gridPricePoints, position));
        return Stream.concat(openPositionGrids, openPositions).toList();
    }

}
