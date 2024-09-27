package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.MemberPointConstraints;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String memberId;

    private String name;

    @Embedded
    private MemberPointConstraints memberPointConstraints;

    @Version
    private Long version;

    public void modifyInfo(String name, MemberPointConstraints constraints) {
        this.name = name;
        memberPointConstraints = constraints;
    }

    public boolean exceedPointLimit(Long point) {
        return point > memberPointConstraints.getMaxAccumulatedPoint();
    }

    public boolean enableToAccumulatePoint(long point) {
        return memberPointConstraints.getMinAccumulatedPointAtOnce() <= point
                && point <= memberPointConstraints.getMaxAccumulatedPointAtOnce();
    }
}
