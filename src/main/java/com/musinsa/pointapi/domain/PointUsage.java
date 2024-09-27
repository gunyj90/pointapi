package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamicUpdate
@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointUsage extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private Long usedPoint;

    private Long originUsedPoint;

    private LocalDateTime usedDateTime;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @OneToMany(mappedBy = "usage", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PointAccumulationUsage> accumulationUsages = new ArrayList<>();

    @Version
    private Long version;

    public static PointUsage of(String orderId, Long usedPoint) {
        return PointUsage.builder()
                .orderId(orderId)
                .usedPoint(usedPoint)
                .originUsedPoint(usedPoint)
                .usedDateTime(LocalDateTime.now())
                .status(PointStatus.USED)
                .build();
    }

    public void addAccumulationUsage(PointAccumulationUsage accumulationUsage) {
        if (accumulationUsages == null) {
            accumulationUsages = new ArrayList<>();
        }

        accumulationUsages.add(accumulationUsage);
        if (accumulationUsage.getUsage() == null) {
            accumulationUsage.setUsage(this);
        }
    }

    public Long getUsedPoint() {
        if (usedPoint <= 0) {
            throw new IllegalStateException("사용 포인트는 0보다 커야합니다.");
        }
        return usedPoint;
    }

    public List<PointAccumulation> cancelAndReturnReaccumulations(Long pointToReverse) {
        if (usedPoint < pointToReverse) {
            throw new IllegalStateException("사용취소 포인트가 실제 사용 포인트보다 작아야합니다. (" + usedPoint + ")");
        }
        usedPoint -= pointToReverse;
        status = usedPoint == 0 ? PointStatus.CANCELED : PointStatus.PARTIALLY_USED;
        return rollbackAccumulationsAndReturnReaccumulations(pointToReverse);
    }

    private List<PointAccumulation> rollbackAccumulationsAndReturnReaccumulations(Long totalPointToBeReversed) {
        Map<PointAccumulation, Long> renewalTarget = new HashMap<>();
        for (PointAccumulationUsage accumulationUsage : accumulationUsages) {
            long partiallyReversedPoint = accumulationUsage.calculatePartiallyReversedPoint(totalPointToBeReversed);

            PointAccumulation accumulation = accumulationUsage.getAccumulation();
            if (accumulation.isExpired()) {
                renewalTarget.merge(accumulation, partiallyReversedPoint, Long::sum);
            } else {
                accumulationUsage.reverseAccumulationPoint(partiallyReversedPoint);
            }
            totalPointToBeReversed -= partiallyReversedPoint;
        }

        return renewalTarget.entrySet()
                .stream()
                .map(entry -> PointAccumulation.renew(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void use(List<PointAccumulation> accumulations) {
        long totalPointToBeUsed = usedPoint;

        for (PointAccumulation accumulation : accumulations) {
            if (totalPointToBeUsed <= 0) {
                break;
            }
            var accumulationUsage = new PointAccumulationUsage(accumulation);
            long partiallyUsedPoint = accumulationUsage.calculatePartiallyUsedPoint(totalPointToBeUsed);
            accumulationUsage.useAccumulationPoint(partiallyUsedPoint);
            this.addAccumulationUsage(accumulationUsage);
            totalPointToBeUsed -= partiallyUsedPoint;
        }

        if (totalPointToBeUsed > 0) {
            throw new IllegalStateException("적립된 포인트가 부족합니다.");
        }
    }
}
