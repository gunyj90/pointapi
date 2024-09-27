package com.musinsa.pointapi.service;

import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.vo.MemberPointConstraints;
import com.musinsa.pointapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Retryable(retryFor = ObjectOptimisticLockingFailureException.class, backoff = @Backoff(delay = 500))
public class MemberService {

    private final MemberRepository memberRepository;

    public Member create(Member member) {
        if (memberRepository.findByMemberId(member.getMemberId())
                .isPresent()) {
            throw new IllegalStateException("중복된 ID가 존재합니다.");
        }
        return memberRepository.save(member);
    }

    public void modify(String memberId, String name, MemberPointConstraints constraints) {
        memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("회원정보가 없습니다."))
                .modifyInfo(name, constraints);
    }

    public Member getMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));
    }
}
