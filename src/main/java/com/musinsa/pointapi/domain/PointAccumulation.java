package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.AccumulationType;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.domain.vo.AvailablePointDuration;
import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.musinsa.pointapi.domain.vo.PointStatus.*;

@Entity
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointAccumulation extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "memberId", updatable = false)
    private Member member;

    private Long originPoint;

    private Long currentPoint;

    @Embedded
    @Column(updatable = false)
    private AvailablePointDuration duration;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private AccumulationType accumulationType;

    private PointAccumulation(Long point) {
        originPoint = point;
        currentPoint = point;
        status = ACCUMULATED;
        accumulationType = AccumulationType.MANUAL;
    }

    public static PointAccumulation of(Member member, Long point, AvailablePointConstraints constraints) {
        PointAccumulation accumulation = new PointAccumulation(point);
        accumulation.setMember(member);
        accumulation.setDuration(constraints);
        accumulation.validateAvailablePoint();
        return accumulation;
    }

    public static PointAccumulation renew(PointAccumulation old, Long point) {
        PointAccumulation accumulation = new PointAccumulation(point);
        accumulation.setMember(old.getMember());
        accumulation.setDuration(
                new AvailablePointConstraints(
                        ChronoUnit.DAYS.between(old.getDuration().getStartDate(), old.getDuration().getEndDate())));
        return accumulation;
    }

    public void cancel() {
        if (!ACCUMULATED.equals(status)) {
            throw new IllegalStateException("해당 적립에 대한 사용 이력이 있거나 이미 취소되었습니다.");
        }
        status = CANCELED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(getDuration().getEndDate());
    }

    public void reduceCurrentPoint(Long pointToUse) {
        currentPoint -= pointToUse;
        status = (currentPoint > 0) ? PARTIALLY_USED : USED;
    }

    public void increaseCurrentPoint(Long point) {
        currentPoint += point;
        status = currentPoint.equals(originPoint) ? ACCUMULATED : PARTIALLY_USED;
    }

    private void setMember(Member member) {
        this.member = member;
    }

    private void setDuration(AvailablePointConstraints constraints) {
        this.duration = new AvailablePointDuration(constraints);
    }

    private void validateAvailablePoint() {
        if (!member.getMemberPointConstraints().enableToAccumulatePoint(currentPoint)) {
            throw new IllegalStateException("1회 적립가능 포인트 범위를 벗어났습니다.");
        }
    }
}
