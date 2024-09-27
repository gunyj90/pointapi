package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.Member;

public record MemberCreationRequest(String memberId, String name, MemberConstraintRequest constraintRequest) {

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .name(name)
                .memberPointConstraints(constraintRequest.toMemberPointConstraints())
                .build();
    }
}
