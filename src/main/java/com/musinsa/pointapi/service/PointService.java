package com.musinsa.pointapi.service;

import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.repository.MemberRepository;
import com.musinsa.pointapi.repository.PointAccumulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointService {

    private final MemberRepository memberRepository;
    private final PointAccumulationRepository pointAccumulationRepository;

    public PointAccumulation accumulate(String memberId, PointAccumulation accumulation) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(IllegalArgumentException::new);
        accumulation.setMember(member);
        accumulation.setDuration(new AvailablePointConstraints());
        accumulation.validateAvailablePoint();
        pointAccumulationRepository.save(accumulation);
        return accumulation;
    }

    public Long cancel(Long accumulationId) {
        return pointAccumulationRepository.findById(accumulationId)
                .orElseThrow(IllegalArgumentException::new)
                .cancel()
                .getPoint();
    }
}
