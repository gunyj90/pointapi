package com.musinsa.pointapi.dto;

import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.vo.AccumulationType;
import com.musinsa.pointapi.domain.vo.PointStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public record PointAccumulationRequest(long point) {

    public PointAccumulation toEntity() {
        return PointAccumulation.builder()
                .point(point)
                .status(PointStatus.ACCUMULATED)
                .accumulationType(AccumulationType.MANUAL)
                .build();
    }
}
