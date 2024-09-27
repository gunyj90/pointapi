package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public final class MemberPointConstraints {

    private Long maxAccumulatedPoint = Long.MAX_VALUE;

    private Long minAccumulatedPointAtOnce = 1L;

    private Long maxAccumulatedPointAtOnce = 100_000L;

    public MemberPointConstraints(Long maxAccumulatedPoint, Long minAccumulatedPointAtOnce, Long maxAccumulatedPointAtOnce) {
        this.maxAccumulatedPoint = maxAccumulatedPoint;
        this.minAccumulatedPointAtOnce = minAccumulatedPointAtOnce;
        this.maxAccumulatedPointAtOnce = maxAccumulatedPointAtOnce;

        if (maxAccumulatedPoint < 0
                || minAccumulatedPointAtOnce < 0
                || maxAccumulatedPointAtOnce < 0
                || maxAccumulatedPointAtOnce > maxAccumulatedPoint
                || maxAccumulatedPoint < minAccumulatedPointAtOnce
                || maxAccumulatedPointAtOnce < minAccumulatedPointAtOnce) {
            throw new IllegalStateException("포인트 제약조건이 잘 못 되었습니다.");
        }
    }
}
