package com.prudentical.botservice.service.bot.grid;

import java.math.BigDecimal;
import java.util.Optional;

record PriceInfo(Optional<Integer> priceGrid, BigDecimal price) {
}