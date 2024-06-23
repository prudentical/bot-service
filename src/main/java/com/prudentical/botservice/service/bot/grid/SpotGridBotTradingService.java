package com.prudentical.botservice.service.bot.grid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.BotConfig;
import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.OrderRequestDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.dto.PriceRequestDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.model.BotExitType;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.ExchangeService;
import com.prudentical.botservice.service.OrderService;
import com.prudentical.botservice.service.bot.BotCrudService;
import com.prudentical.botservice.service.bot.BotTradingService;

import lombok.Builder;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpotGridBotTradingService implements BotTradingService {

    private record PriceInfo(Optional<Integer> priceGrid, BigDecimal price) {
    }

    @With
    @Builder
    private record BotContext(
            long userId,
            long accountId,
            SpotGridBot bot,
            List<PositionDTO> openPositions,
            Optional<Integer> previousGrid,
            BigDecimal gridUnitFund,
            Map<Integer, BigDecimal> gridPricePoints) {
    }

    private final BotCrudService<SpotGridBot> crud;

    private final ExchangeService exchange;

    private final OrderService order;

    private final Grider grider;

    private final Duration coolDown;

    private final Duration updateFreq;

    @Autowired
    public SpotGridBotTradingService(BotCrudService<SpotGridBot> crud,
            ExchangeService exchange,
            OrderService order,
            Grider grider,
            BotConfig botConfig) {
        this.crud = crud;
        this.exchange = exchange;
        this.order = order;
        this.grider = grider;
        this.coolDown = botConfig.coolDown();
        this.updateFreq = botConfig.UpdateFreq();
    }

    @Override
    public void run(long userId, long accountId, long id) {
        log.info("SpotGridBot with id[{}] started", id);
        var bot = crud.getById(userId, accountId, id);
        var context = setupBotContext(userId, accountId, id, bot);
        while (bot.isActive()) {
            context = tryToBuy(context);
            context = tryToSell(context);
            context = tryToStopLoss(context);
            context = update(context);
            throttle();
        }
    }

    private BotContext setupBotContext(long userId, long accountId, long id, SpotGridBot bot) {
        var positions = order.getOpenPositions(userId, accountId, id);
        var gridUnitFund = bot.getCapital().divide(BigDecimal.valueOf(bot.getGrids()), 30, RoundingMode.HALF_UP);
        var gridPrices = this.grider.getGridPricePoints(bot.getCeiling(), bot.getFloor(), bot.getGrids());
        var context = BotContext.builder()
                .userId(userId)
                .accountId(accountId)
                .bot(bot)
                .gridPricePoints(gridPrices)
                .gridUnitFund(gridUnitFund)
                .openPositions(positions)
                .previousGrid(Optional.empty())
                .build();
        return context;
    }

    private BotContext tryToBuy(BotContext context) {
        var currentPrice = getCurrentPrice(context, TradeType.Sell);
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

    private BotContext tryToSell(BotContext context) {
        var priceInfo = getCurrentPrice(context, TradeType.Sell);

        Predicate<PositionDTO> inProfit = position -> priceInfo.priceGrid()
                .filter(grid -> grid == getPositionGrid(context.gridPricePoints(), position) + 2)
                .isPresent();

        context.openPositions().stream()
                .filter(inProfit)
                .findFirst()
                .ifPresent(position -> closePosition(context, priceInfo.price(), position));

        return context;
    }

    private BotContext tryToStopLoss(BotContext context) {
        if (context.bot().getStopLoss() == null) {
            return context;
        }
        var priceInfo = getCurrentPrice(context, TradeType.Sell);

        var passedStopLoss = priceInfo.price().compareTo(context.bot().getStopLoss()) <= 0;
        if (passedStopLoss) {
            context.openPositions()
                    .forEach(position -> closePosition(context, priceInfo.price(), position));
            context.bot().setActive(false);
            context.bot().setExitType(BotExitType.StopLoss);
        }
        return context;
    }

    private List<Integer> getOpenPositionGrids(BotContext context) {
        var gridPricePoints = context.gridPricePoints();
        var openPositionGrids = context.bot().getOpenPositionGrids().stream();
        var openPositions = context.openPositions().stream()
                .map(position -> getPositionGrid(gridPricePoints, position));
        return Stream.concat(openPositionGrids, openPositions).toList();
    }

    private BotContext update(BotContext context) {
        var bot = context.bot();
        if (bot.getLastCheck() != null && Instant.now().minus(Duration.ofMillis(5)).isBefore(bot.getLastCheck())) {
            throw new RuntimeException("Update timeout");
        }
        if (bot.getLastCheck() == null || Instant.now().minus(updateFreq).isBefore(bot.getLastCheck())) {
            bot.setLastCheck(Instant.now());
            var active = crud.getById(context.userId(), context.accountId(), context.bot().getId()).isActive();
            bot.setActive(active);
            crud.update(context.userId(), context.accountId(), context.bot().getId(), bot);
            bot = crud.getById(context.userId(), context.accountId(), context.bot().getId());
            var positions = order.getOpenPositions(context.userId(), context.accountId(), context.bot().getId());
            context = context.withOpenPositions(positions);
        }
        return context.withBot(bot);
    }

    private PriceInfo getCurrentPrice(BotContext context, TradeType type) {
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

    private void closePosition(BotContext context, BigDecimal price, PositionDTO openPosition) {
        var amount = openPosition.orders().stream()
                .filter(order -> order.type().equals(TradeType.Buy))
                .map(OrderDTO::amount).reduce(BigDecimal::add).get();
        var orderRequest = OrderRequestDTO.builder()
                .botId(context.bot().getId())
                .amount(amount)
                .price(price)
                .type(TradeType.Sell)
                .build();
        this.exchange.order(context.bot().getExchangeId(), context.bot().getPairId(), orderRequest);

        var openPositions = context.bot().getOpenPositionGrids().stream()
                .filter(i -> i != getPositionGrid(context.gridPricePoints(), openPosition))
                .toList();
        context.bot().setOpenPositionGrids(openPositions);
    }

    private int getPositionGrid(Map<Integer, BigDecimal> gridPricePoints, PositionDTO position) {
        var positionPrice = position.orders().getFirst().price();
        var positionGrid = this.grider.getPriceGrid(gridPricePoints, positionPrice);
        return positionGrid.get();
    }

    private void throttle() {
        try {
            Thread.sleep(coolDown);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
