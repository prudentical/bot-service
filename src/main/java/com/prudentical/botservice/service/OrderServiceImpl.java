package com.prudentical.botservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.prudentical.botservice.dto.OrderDTO;
import com.prudentical.botservice.dto.PositionDTO;
import com.prudentical.botservice.persistence.Page;

@Service
public class OrderServiceImpl implements OrderService {

    private final String endpoint = "http://order-service/users/{userId}/accounts/{accountId}/bots/{id}/positions";

    private final RestTemplate restTemplate;

    @Autowired
    public OrderServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PositionDTO> getOpenPositions(long userId, long accountId, long botId) {
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .query("status=Open")
                .buildAndExpand(userId, accountId, botId).toUri();
        var page = this.restTemplate.getForObject(uri, Page.class);
        // TODO: make sure page contains that all open positions
        return Optional.ofNullable(page).map(Page::list).orElse(List.of());
    }

    @Override
    public OrderDTO getOrder(long userId, long accountId, long botId, String internalId) {
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .query("internalId={internalId}")
                .buildAndExpand(userId, accountId, botId, internalId)
                .toUri();
        var order = this.restTemplate.getForObject(uri, OrderDTO.class);
        return order;
    }

}
