package com.prudentical.botservice.service.bot.grid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import com.prudentical.botservice.BotConfig;
import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.model.BotExitType;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.ExchangeService;
import com.prudentical.botservice.service.OrderService;
import com.prudentical.botservice.service.bot.BotCrudService;

@ExtendWith(MockitoExtension.class)
public class SpotGridBotTradingServiceTest {

    @Mock
    private BotCrudService<SpotGridBot> crud;

    @Mock
    private ExchangeService exchange;

    @Mock
    private OrderService order;

    private Grider grider = new GriderImpl();

    private SpotGridBotTradingService service;

    private SpotGridBot bot;

    @BeforeEach
    void beforeEach() {
        service = new SpotGridBotTradingService(crud, exchange, order, grider,new BotConfig(Duration.ZERO, Duration.ZERO));
        bot = SpotGridBot.builder()
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
    }

    @Test
    void start_singleBuy_shouldPlaceOrder() {
        bot = spy(bot);
        when(bot.isActive()).thenReturn(true, true, true, false);
        when(crud.getById(0, 0, 0)).thenReturn(bot);
        when(order.getOpenPositions(0, 0, 0))
                .thenReturn(List.of());
        when(exchange.getPriceFor(anyLong(), anyLong(), any()))
                .thenReturn(
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(85));
        service.run(0, 0, 0);
        assertThat(bot.getOpenPositionGrids())
                .hasSize(1)
                .contains(4);
    }

    @Test
    @SuppressWarnings("unchecked")
    void start_singleBuyAndSell_shouldPlaceOrder() {
        bot = spy(bot);
        var order = OrderDTO.builder().type(TradeType.Buy)
                .amount(BigDecimal.valueOf(1)).price(BigDecimal.valueOf(75))
                .build();
        var position = PositionDTO.builder()
                .orders(List.of(order))
                .build();
        when(bot.isActive()).thenReturn(true, true, false);
        when(this.crud.getById(0, 0, 0)).thenReturn(bot);
        when(this.order.getOpenPositions(0, 0, 0))
                .thenReturn(List.of(), List.of(position));
        when(this.exchange.getPriceFor(anyLong(), anyLong(), any()))
                .thenReturn(
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(75),
                        BigDecimal.valueOf(95));
        this.service.run(0, 0, 0);
        assertThat(bot.getOpenPositionGrids()).isEmpty();
    }

    @Test
    void start_singleBuyAndHitStopLoss_shouldPlaceOrder() {
        bot = spy(bot);
        var order = OrderDTO.builder().type(TradeType.Buy)
                .amount(BigDecimal.valueOf(1)).price(BigDecimal.valueOf(75))
                .build();
        var position = PositionDTO.builder()
                .orders(List.of(order))
                .build();
        when(this.crud.getById(0, 0, 0)).thenReturn(bot);
        when(this.order.getOpenPositions(0, 0, 0))
                .thenReturn(List.of(position));
        when(this.exchange.getPriceFor(anyLong(), anyLong(), any()))
                .thenReturn(
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(95),
                        BigDecimal.valueOf(75),
                        BigDecimal.valueOf(30));
        this.service.run(0, 0, 0);
        assertThat(bot.getOpenPositionGrids()).isEmpty();
        assertThat(bot.isActive()).isFalse();
        assertThat(bot.getExitType()).isEqualTo(BotExitType.StopLoss);
    }
}
