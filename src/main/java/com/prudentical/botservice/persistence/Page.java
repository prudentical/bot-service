package com.prudentical.botservice.persistence;

import java.util.List;

import lombok.Builder;

@Builder
public record Page<T>(List<T> list, int page, int size, long total) {

}
