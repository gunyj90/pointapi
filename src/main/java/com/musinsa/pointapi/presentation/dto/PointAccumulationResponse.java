package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.PointAccumulation;
import lombok.Builder;

@Builder
public record PointAccumulationResponse(Long accumulationId, Long point) {

    public static PointAccumulationResponse from(PointAccumulation accumulation) {
        return PointAccumulationResponse.builder()
                .accumulationId(accumulation.getId())
                .point(accumulation.getOriginPoint())
                .build();
    }
}
