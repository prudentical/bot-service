package com.prudentical.botservice.service.bot.grid;

import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.BotConfig;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.OrderService;
import com.prudentical.botservice.service.bot.BotCrudService;
import com.prudentical.botservice.service.bot.BotTradingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpotGridBotTradingService implements BotTradingService {

    private final BotCrudService<SpotGridBot> crud;

    private final OrderService order;

    private final Grider grider;

    private final SpotGridBuyerService buyer;

    private final SpotGridSellerService seller;

    private final SpotGridStopLossService stoper;

    private final Duration coolDown;

    private final Duration updateFreq;

    @Autowired
    public SpotGridBotTradingService(BotCrudService<SpotGridBot> crud,
            OrderService order,
            Grider grider,
            SpotGridBuyerService buyer,
            SpotGridSellerService seller,
            SpotGridStopLossService stoper,
            BotConfig botConfig) {
        this.crud = crud;
        this.order = order;
        this.grider = grider;
        this.buyer = buyer;
        this.seller = seller;
        this.stoper = stoper;
        this.coolDown = botConfig.coolDown();
        this.updateFreq = botConfig.updateFreq();
    }

    @Override
    public void run(long userId, long accountId, long id) {
        log.info("SpotGridBot with id[{}] started", id);
        var bot = this.crud.getById(userId, accountId, id);
        var context = setupBotContext(userId, accountId, id, bot);
        while (context.bot().isActive()) {
            context = this.buyer.tryToBuy(context);
            context = this.seller.tryToSell(context);
            context = this.stoper.tryToStopLoss(context);
            context = update(context);
            throttle();
        }
    }

    private BotContext setupBotContext(long userId, long accountId, long id, SpotGridBot bot) {
        var positions = this.order.getOpenPositions(userId, accountId, id);
        var gridUnitFund = bot.getCapital().divide(BigDecimal.valueOf(bot.getGrids()), 30, RoundingMode.HALF_UP);
        var gridPrices = this.grider.getGridPricePoints(bot.getFloor(), bot.getCeiling(), bot.getGrids());
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
            var positions = this.order.getOpenPositions(context.userId(), context.accountId(), context.bot().getId());
            context = context.withOpenPositions(positions);
        }
        return context.withBot(bot);
    }

    private void throttle() {
        try {
            Thread.sleep(coolDown);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
