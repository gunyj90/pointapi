package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class MemberPointConstraints {

    private Long maxAccumulatedPoint = Long.MIN_VALUE;

    private Long minAccumulatedPointAtOnce = 1L;

    private Long maxAccumulatedPointAtOnce = 100_000L;

    public boolean enableToAccumulatePoint(long point) {
        return minAccumulatedPointAtOnce <= point && point <= maxAccumulatedPointAtOnce;
    }
}
