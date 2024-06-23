package com.prudentical.botservice.dto;

import java.util.List;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record PositionDTO(long id, PositionStatus status, List<OrderDTO> orders) {

}

