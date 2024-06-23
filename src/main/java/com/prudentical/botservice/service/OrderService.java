package com.prudentical.botservice.service;

import java.util.List;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;

public interface OrderService {
    List<PositionDTO> getOpenPositions(long userId, long accountId, long botId);

    OrderDTO getOrder(long userId, long accountId, long botId, String internalId);
}
