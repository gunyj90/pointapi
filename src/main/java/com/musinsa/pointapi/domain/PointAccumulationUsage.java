package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointAccumulationUsage extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accumulation_id")
    private PointAccumulation accumulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_id")
    private PointUsage usage;

    private Long partiallyUsedPoint;

    private Long originPartiallyUsedPoint;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @Version
    private Long version;

    public PointAccumulationUsage(PointAccumulation accumulation) {
        this.accumulation = accumulation;
        status = PointStatus.USED;
    }

    public void setUsage(PointUsage usage) {
        this.usage = usage;
        if (!usage.getAccumulationUsages().contains(this)) {
            usage.getAccumulationUsages().add(this);
        }
    }

    public Long calculatePartiallyUsedPoint(Long totalPointToBeUsed) {
        return Math.min(totalPointToBeUsed, accumulation.getCurrentPoint());
    }

    public Long calculatePartiallyReversedPoint(Long totalPointToBeReversed) {
        return Math.min(partiallyUsedPoint, totalPointToBeReversed);
    }

    public void useAccumulationPoint(Long pointToBeUsed) {
        accumulation.reduceCurrentPoint(pointToBeUsed);
        partiallyUsedPoint = pointToBeUsed;
        originPartiallyUsedPoint = pointToBeUsed;
    }

    public void reverseAccumulationPoint(Long pointToBeReversed) {
        partiallyUsedPoint -= pointToBeReversed;
        status = partiallyUsedPoint == 0 ? PointStatus.CANCELED : PointStatus.PARTIALLY_USED;
        accumulation.increaseCurrentPoint(pointToBeReversed);
    }
}
