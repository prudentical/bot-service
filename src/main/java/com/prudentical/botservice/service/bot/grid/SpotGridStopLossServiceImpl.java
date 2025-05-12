package com.prudentical.botservice.service.bot.grid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.model.BotExitType;

@Service
public class SpotGridStopLossServiceImpl implements SpotGridStopLossService {

    private final PriceService price;

    private final SpotGridSellerService seller;

    @Autowired
    public SpotGridStopLossServiceImpl(PriceService price, SpotGridSellerService seller) {
        this.price = price;
        this.seller = seller;
    }

    @Override
    public BotContext tryToStopLoss(BotContext context) {
        if (context.bot().getStopLoss() == null) {
            return context;
        }
        var priceInfo = this.price.getCurrentPriceInfo(context, TradeType.Sell);

        var passedStopLoss = priceInfo.price().compareTo(context.bot().getStopLoss()) <= 0;
        if (passedStopLoss) {
            context.openPositions()
                    .forEach(position -> this.seller.closePosition(context, priceInfo.price(), position));
            context.bot().setActive(false);
            context.bot().setExitType(BotExitType.StopLoss);
        }
        return context;
    }

}
