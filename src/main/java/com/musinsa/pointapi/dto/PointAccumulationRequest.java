package com.musinsa.pointapi.dto;

import com.musinsa.pointapi.domain.PointAccumulation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PointAccumulationRequest {

    private final long point;

    public PointAccumulation toEntity() {
        return PointAccumulation.builder()
                .point(point)
                .build();
    }
}
