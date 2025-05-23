package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;

@Component
public class GriderImpl implements Grider {

    @Override
    public Map<Integer, BigDecimal> getGridPricePoints(BigDecimal floor, BigDecimal ceiling, int grids) {
        var diff = ceiling.subtract(floor);
        var gridUnitPrice = diff.divide(BigDecimal.valueOf(grids), 30, RoundingMode.HALF_UP);
        var gridPrices = IntStream.rangeClosed(1, grids)
                .mapToObj(BigDecimal::valueOf)
                .collect(Collectors.toMap(BigDecimal::intValue, grid -> calcGridPrice(floor, gridUnitPrice, grid)));
        return gridPrices;
    }

    @Override
    public Optional<Integer> getPriceGrid(Map<Integer, BigDecimal> gridPricePoints, BigDecimal price) {
        return gridPricePoints.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .dropWhile(grid -> grid.getValue().compareTo(price) < 0)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private BigDecimal calcGridPrice(BigDecimal floor, BigDecimal gridUnitPrice, BigDecimal grid) {
        var increase = gridUnitPrice.multiply(grid);
        var price = floor.add(increase);
        return price;
    }

    @Override
    public int getPositionGrid(Map<Integer, BigDecimal> gridPricePoints, PositionDTO position) {
        var positionPrice = position.orders().stream().filter(OrderDTO::isBuyOrder).findFirst().get().price();
        var positionGrid = getPriceGrid(gridPricePoints, positionPrice);
        return positionGrid.get();
    }
}
