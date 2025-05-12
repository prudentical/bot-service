package com.prudentical.botservice.service.bot.grid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.model.SpotGridBot;

@ExtendWith(MockitoExtension.class)
public class SpotGridBotStopLossServiceTest {

    @Mock
    private PriceService price;

    @Mock
    private SpotGridSellerService seller;

    private SpotGridStopLossService service;

    @BeforeEach
    void beforeEach() {
        service = new SpotGridStopLossServiceImpl(price, seller);
    }

    @Test
    void sell_whenPriceGoesToHigherGrid_shouldPlaceOrder() {
        var bot = SpotGridBot.builder()
                .active(true)
                .id(1L)
                .exchangeId(1L)
                .pairId(1L)
                .capital(BigDecimal.valueOf(50L))
                .openPositionGrids(List.of(1))
                .stopLoss(BigDecimal.valueOf(40L))
                .grids(5)
                .build();
        var orders = List.of(
                OrderDTO.builder()
                        .amount(BigDecimal.valueOf(1))
                        .price(BigDecimal.valueOf(55L))
                        .type(TradeType.Buy)
                        .build());
        var positions = List.of(
                PositionDTO.builder().orders(orders).build());
        var context = BotContext.builder()
                .bot(bot)
                .openPositions(new ArrayList<>(positions))
                .previousGrid(Optional.of(4))
                .build();

        when(price.getCurrentPriceInfo(any(), any()))
                .thenReturn(new PriceInfo(Optional.of(3), BigDecimal.valueOf(35L)));

        var result = service.tryToStopLoss(context);
        assertThat(result.bot().isActive())
                .isFalse();
    }

}
