package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.dto.TradeType;

import static org.assertj.core.api.Assertions.assertThat;

public class GriderTest {

    private Grider grider = new GriderImpl();

    @Test
    void getGridPricePoints_withFiveGrid_shouldTheResult() {
        var ceiling = BigDecimal.valueOf(100);
        var floor = BigDecimal.valueOf(50);
        var grids = 5;
        var pricePoints = grider.getGridPricePoints(floor, ceiling, grids);
        assertThat(pricePoints)
                .hasSize(5)
                .containsKeys(1, 2, 3, 4, 5)
                .anySatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(60)))
                .anySatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(70)))
                .anySatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(80)))
                .anySatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(90)))
                .anySatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(100)))
                .noneSatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(110)))
                .noneSatisfy((grid, price) -> assertThat(price).isEqualByComparingTo(BigDecimal.valueOf(50)));
    }

    @Test
    void getPriceGrid_withFiveGrid_shouldTheResult() {
        var pricePoints = Map.of(
                1, BigDecimal.valueOf(60),
                2, BigDecimal.valueOf(70),
                3, BigDecimal.valueOf(80),
                4, BigDecimal.valueOf(90),
                5, BigDecimal.valueOf(100));
        var price = BigDecimal.valueOf(95);
        var grid = grider.getPriceGrid(pricePoints, price);
        assertThat(grid).hasValue(5);
    }

    @Test
    void getPositionGrid_withLastGridPrice_shouldRetunLastGrid() {
        var pricePoints = Map.of(
                1, BigDecimal.valueOf(60),
                2, BigDecimal.valueOf(70),
                3, BigDecimal.valueOf(80),
                4, BigDecimal.valueOf(90),
                5, BigDecimal.valueOf(100));
        var orders = List.of(OrderDTO.builder().type(TradeType.Buy).price(BigDecimal.valueOf(95)).build());
        var position = PositionDTO.builder().orders(orders).build();
        var grid = grider.getPositionGrid(pricePoints, position);
        assertThat(grid).isEqualTo(5);
    }
}
