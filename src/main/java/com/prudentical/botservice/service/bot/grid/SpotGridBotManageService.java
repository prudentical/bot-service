package com.prudentical.botservice.service.bot.grid;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.exceptions.IllegalStateException;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.bot.BotCrudService;
import com.prudentical.botservice.service.bot.BotManageService;

@Service
public class SpotGridBotManageService implements BotManageService {

    private final BotCrudService<SpotGridBot> curd;

    private final SpotGridBotTradingService trading;

    @Autowired
    public SpotGridBotManageService(BotCrudService<SpotGridBot> curd, SpotGridBotTradingService trading) {
        this.curd = curd;
        this.trading = trading;
    }

    @Override
    public void start(long userId, long accountId, long id) {
        var bot = curd.getById(userId, accountId, id);
        if (bot.isActive())
            throw IllegalStateException.botAlreadyRunning();
        bot.setActive(true);
        curd.update(userId, accountId, id, bot);
        Thread.ofVirtual().start(() -> this.trading.run(userId, accountId, id));
    }

    @Override
    public void stop(long userId, long accountId, long id) {
        var bot = curd.getById(userId, accountId, id);
        bot.setActive(false);
        curd.update(userId, accountId, id, bot);
    }

    @Override
    public boolean isRunning(long userId, long accountId, long id) {
        var bot = curd.getById(userId, accountId, id);
        var oneMinAgo = Instant.now().minusSeconds(60);
        return bot.getLastCheck().isAfter(oneMinAgo);
    }

}
