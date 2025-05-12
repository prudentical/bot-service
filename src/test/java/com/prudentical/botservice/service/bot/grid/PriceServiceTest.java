package com.prudentical.botservice.service.bot.grid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prudentical.botservice.dto.TradeType;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.service.ExchangeService;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    @Mock
    private ExchangeService exchange;

    private Grider grider = new GriderImpl();

    private PriceService service;

    @BeforeEach
    void beforeEach() {
        service = new PriceServiceImpl(exchange, grider);
    }

    @Test
    void getCurrentPriceInfo_forBuyType_shouldReturnPrice() {
        var context = BotContext.builder()
                .bot(SpotGridBot.builder().exchangeId(0L).pairId(0L).build())
                .gridUnitFund(BigDecimal.valueOf(10L))
                .gridPricePoints(Map.of(
                        1, BigDecimal.valueOf(60L),
                        2, BigDecimal.valueOf(70L),
                        3, BigDecimal.valueOf(80L),
                        4, BigDecimal.valueOf(90L),
                        5, BigDecimal.valueOf(100L)))
                .build();
        when(this.exchange.getPriceFor(anyLong(), anyLong(), any()))
                .thenReturn(
                        BigDecimal.valueOf(95));
        var price = this.service.getCurrentPriceInfo(context, TradeType.Buy);

        assertThat(price.price()).isEqualTo("95");
        assertThat(price.priceGrid()).hasValue(5);
    }
}
