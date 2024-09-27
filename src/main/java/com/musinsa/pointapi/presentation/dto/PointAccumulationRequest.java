package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;

public record PointAccumulationRequest(long point, AvailablePointConstraints constraints) {
}
