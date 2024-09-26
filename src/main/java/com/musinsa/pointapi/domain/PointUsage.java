package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public PointUsage cancel(Long pointToReverse) {
        if (usedPoint < pointToReverse) {
            throw new IllegalStateException("사용취소 포인트가 실제 사용 포인트보다 작아야합니다.");
        }
        usedPoint -= pointToReverse;
        status = usedPoint == 0 ? PointStatus.CANCELED : PointStatus.PARTIALLY_USED;
        return this;
    }
}
