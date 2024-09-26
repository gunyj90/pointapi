package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.PointUsage;

public record PointUsageRequest(String orderId, long point) {

    public PointUsage toEntity() {
        return PointUsage.of(orderId, point);
    }
}
