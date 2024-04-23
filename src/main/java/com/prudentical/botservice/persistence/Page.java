package com.prudentical.botservice.persistence;

import java.util.List;

import lombok.Builder;

@Builder
public record Page<T>(List<T> content, int page, int size, long total) {

}
