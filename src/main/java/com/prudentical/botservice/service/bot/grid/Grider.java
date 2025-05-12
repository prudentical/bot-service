package com.prudentical.botservice.service.bot.grid;

import java.util.Map;
import java.util.Optional;

import com.prudentical.botservice.dto.PositionDTO;

import java.math.BigDecimal;

public interface Grider {

    Map<Integer, BigDecimal> getGridPricePoints(BigDecimal floor, BigDecimal ceiling, int grids);

    Optional<Integer> getPriceGrid(Map<Integer, BigDecimal> gridPricePoints, BigDecimal price);

    int getPositionGrid(Map<Integer, BigDecimal> gridPricePoints, PositionDTO position);

}
