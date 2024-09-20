package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.MemberPointConstraints;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
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
}
