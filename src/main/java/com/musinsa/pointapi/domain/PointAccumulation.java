package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.AccumulationType;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.domain.vo.AvailablePointDuration;
import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
    @JoinColumn(name = "member_id", referencedColumnName = "memberId")
    @Column(updatable = false)
    private Member member;

    private Long point;

    @Embedded
    @Column(updatable = false)
    private AvailablePointDuration duration;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private AccumulationType accumulationType;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setDuration(AvailablePointConstraints constraints) {
        this.duration = new AvailablePointDuration(constraints);
    }

    public void validateAvailablePoint() {
        if (member.getMemberPointConstraints().enableToAccumulatePoint(point)) {
            throw new IllegalStateException("1ȸ ���� ���� ����Ʈ�� ������ ������ϴ�.");
        }
    }

    public PointAccumulation cancel() {
        if (!PointStatus.ACCUMULATED.equals(status)) {
            throw new IllegalStateException("���� �� ��������Ʈ�Դϴ�.");
        }
        status = PointStatus.CANCELED;
        return this;
    }
}
