package com.musinsa.pointapi.service;

import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.PointAccumulationUsage;
import com.musinsa.pointapi.domain.PointUsage;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.repository.MemberRepository;
import com.musinsa.pointapi.repository.PointAccumulationRepository;
import com.musinsa.pointapi.repository.PointAccumulationUsageRepository;
import com.musinsa.pointapi.repository.PointUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class PointService {

    private final MemberRepository memberRepository;
    private final PointAccumulationRepository pointAccumulationRepository;
    private final PointUsageRepository pointUsageRepository;
    private final PointAccumulationUsageRepository pointAccumulationUsageRepository;

    public PointAccumulation accumulate(String memberId, Long point) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));
        return createAccumulation(member, point);
    }

    public void cancelAccumulation(Long accumulationId) {
        pointAccumulationRepository.findById(accumulationId)
                .orElseThrow(() -> new IllegalStateException("적립이력이 존재하지 않습니다."))
                .cancel();
    }

    public PointUsage use(String memberId, PointUsage usage) {
        List<PointAccumulation> accumulations = findAccumulationsByMemberId(memberId);
        minusAccumulationPointByUsage(accumulations, usage);
        pointUsageRepository.save(usage);
        return usage;
    }

    public void cancelUsage(Long usageId, Long totalPointToBeReversed) {
        PointUsage usage = pointUsageRepository.findToCancel(usageId)
                .orElseThrow(() -> new IllegalStateException("사용이력이 존재하지 않습니다."))
                .cancel(totalPointToBeReversed);

        Map<PointAccumulation, Long> renewalTarget = plusAccumulationPointByCancel(totalPointToBeReversed, usage);
        if (!renewalTarget.isEmpty()) {
            pointAccumulationRepository.saveAll(
                    renewalTarget.entrySet()
                            .stream()
                            .map(entry -> PointAccumulation.renew(entry.getKey(), entry.getValue()))
                            .toList());
        }

    }

    private static Map<PointAccumulation, Long> plusAccumulationPointByCancel(Long totalPointToBeReversed, PointUsage usage) {
        Map<PointAccumulation, Long> renewalTarget = new HashMap<>();
        for (PointAccumulationUsage accumulationUsage : usage.getAccumulationUsages()) {
            long partiallyReversedPoint = accumulationUsage.calculatePartiallyReversedPoint(totalPointToBeReversed);

            PointAccumulation accumulation = accumulationUsage.getAccumulation();
            if (accumulation.isExpired()) {
                renewalTarget.merge(accumulation, partiallyReversedPoint, Long::sum);
            } else {
                accumulationUsage.reverseAccumulationPoint(partiallyReversedPoint);
            }
            totalPointToBeReversed -= partiallyReversedPoint;
        }
        return renewalTarget;
    }

    private void minusAccumulationPointByUsage(List<PointAccumulation> accumulations, PointUsage usage) {
        Long totalPointToBeUsed = usage.getUsedPoint();

        for (PointAccumulation accumulation : accumulations) {
            if (totalPointToBeUsed <= 0) {
                break;
            }
            var accumulationUsage = new PointAccumulationUsage(accumulation);
            long partiallyUsedPoint = accumulationUsage.calculatePartiallyUsedPoint(totalPointToBeUsed);
            accumulationUsage.useAccumulationPoint(partiallyUsedPoint);
            usage.addAccumulationUsage(accumulationUsage);
            totalPointToBeUsed -= partiallyUsedPoint;
        }

        if (totalPointToBeUsed > 0) {
            throw new IllegalStateException("적립된 포인트가 부족합니다.");
        }
    }

    private PointAccumulation createAccumulation(Member member, Long point) {
        var accumulation = PointAccumulation.of(member, point, new AvailablePointConstraints());
        return pointAccumulationRepository.save(accumulation);
    }

    private List<PointAccumulation> findAccumulationsByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));
        return pointAccumulationRepository.findToBeUsed(member);
    }

}
