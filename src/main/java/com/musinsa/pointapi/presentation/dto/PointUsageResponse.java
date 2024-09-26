package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.PointUsage;
import lombok.Builder;

@Builder
public record PointUsageResponse(Long usageId, Long point) {

    public static PointUsageResponse from(PointUsage usage) {
        return PointUsageResponse.builder()
                .usageId(usage.getId())
                .point(usage.getUsedPoint())
                .build();
    }
}
