package com.musinsa.pointapi.service;

import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointService {

    private final MemberRepository memberRepository;

    public long accumulate(String memberId, PointAccumulation accumulation) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(IllegalArgumentException::new);
        accumulation.setMember(member);
        return accumulation.getPoint();
    }
}
