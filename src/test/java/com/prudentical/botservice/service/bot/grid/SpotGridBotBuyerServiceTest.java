package com.prudentical.botservice.service.bot.grid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.ExchangeService;

@ExtendWith(MockitoExtension.class)
public class SpotGridBotBuyerServiceTest {

    @Mock
    private ExchangeService exchange;

    @Mock
    private PriceService price;

    private final Grider grider = new GriderImpl();

    private SpotGridBuyerService service;

    @BeforeEach
    void beforeEach() {
        service = new SpotGridBuyerServiceImpl(exchange, grider, price);
    }

    @Test
    void buy_whenPriceGoesToLowerGrid_shouldPlaceOrder() {
        var gridPrices = this.grider
                .getGridPricePoints(BigDecimal.valueOf(50L), BigDecimal.valueOf(100L), 5);
        var bot = SpotGridBot.builder()
                .id(1L)
                .exchangeId(1L)
                .pairId(1L)
                .capital(BigDecimal.valueOf(50L))
                .grids(5)
                .build();

        var context = BotContext.builder()
                .bot(bot)
                .openPositions(new ArrayList<>())
                .gridPricePoints(gridPrices)
                .previousGrid(Optional.of(4))
                .build();

        when(price.getCurrentPriceInfo(any(), any()))
                .thenReturn(new PriceInfo(Optional.of(3), BigDecimal.valueOf(75L)));

        var result = service.tryToBuy(context);
        assertThat(result.bot().getOpenPositionGrids())
                .hasSize(1);
    }

}
