package com.prudentical.botservice.service.bot.grid;

import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

public interface Grider {

    Map<Integer, BigDecimal> getGridPricePoints(BigDecimal ceiling, BigDecimal floor, int grids);

    Optional<Integer> getPriceGrid(Map<Integer, BigDecimal> gridPricePoints, BigDecimal price);

}
