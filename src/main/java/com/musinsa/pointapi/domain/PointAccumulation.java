package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.AvailablePointDuration;
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
    private Member member;

    private Long point;

    @Embedded
    private AvailablePointDuration duration;

    public void setMember(Member member) {
        this.member = member;
    }

}
