package com.prudentical.botservice.service.bot.grid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prudentical.botservice.BotConfig;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.OrderService;
import com.prudentical.botservice.service.bot.BotCrudService;

@ExtendWith(MockitoExtension.class)
public class SpotGridBotTradingServiceTest {

    @Mock
    private BotCrudService<SpotGridBot> crud;

    @Mock
    private OrderService order;

    @Mock
    private SpotGridBuyerService buyer;

    @Mock
    private SpotGridSellerService seller;

    @Mock
    private SpotGridStopLossService stoper;

    private Grider grider = new GriderImpl();

    private SpotGridBotTradingService service;

    @BeforeEach
    void beforeEach() {
        var config = new BotConfig(Duration.ZERO, Duration.ZERO);
        service = new SpotGridBotTradingService(crud, order, grider, buyer, seller,
                stoper, config);
    }

    @Test
    void start_whenDeactivated_shouldStopAfterOneIteration() {
        var bot = SpotGridBot.builder()
                .id(0L)
                .accountId(0L)
                .exchangeId(0L)
                .pairId(0L)
                .active(true)
                .grids(5)
                .capital(BigDecimal.valueOf(100))
                .ceiling(BigDecimal.valueOf(100))
                .floor(BigDecimal.valueOf(50))
                .stopLoss(BigDecimal.valueOf(45))
                .build();
        bot = spy(bot);
        when(bot.isActive()).thenReturn(true, false);
        when(this.crud.getById(0, 0, 0)).thenReturn(bot);
        when(this.buyer.tryToBuy(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(this.seller.tryToSell(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(this.stoper.tryToStopLoss(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(this.crud.getById(0, 0, 0)).thenReturn(bot);

        this.service.run(0, 0, 0);

        verify(buyer, times(1)).tryToBuy(any());
        verify(seller, times(1)).tryToSell(any());
        verify(stoper, times(1)).tryToStopLoss(any());
    }

}
