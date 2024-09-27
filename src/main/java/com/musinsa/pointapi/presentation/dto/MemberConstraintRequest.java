package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.vo.MemberPointConstraints;

public record MemberConstraintRequest(Long maxPoint, Long minPointAtOnce, Long maxPointAtOnce) {

    public MemberPointConstraints toMemberPointConstraints() {
        return new MemberPointConstraints(maxPoint, minPointAtOnce, maxPointAtOnce);
    }
}
