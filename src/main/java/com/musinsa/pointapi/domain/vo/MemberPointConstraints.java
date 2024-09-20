package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class MemberPointConstraints {

    private Long maxAccumulatedPoint = Long.MIN_VALUE;

    private Long minAccumulatedPointAtOnce = 1L;

    private Long maxAccumulatedPointAtOnce = 100_000L;
}
