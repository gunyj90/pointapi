package com.musinsa.pointapi.service;

import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.PointUsage;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.repository.PointAccumulationRepository;
import com.musinsa.pointapi.repository.PointUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Retryable(retryFor = ObjectOptimisticLockingFailureException.class, backoff = @Backoff(delay = 500))
@Transactional
public class PointService {

    private final MemberService memberService;
    private final PointAccumulationRepository pointAccumulationRepository;
    private final PointUsageRepository pointUsageRepository;

    public PointAccumulation accumulate(String memberId, Long point, AvailablePointConstraints constraints) {
        Member member = memberService.getMember(memberId);
        Long sumPoint = pointAccumulationRepository.summarizePointAccumulated(member);
        if (member.exceedPointLimit(sumPoint + point)) {
            throw new IllegalStateException("최대 적립 포인트를 초과했습니다.");
        }
        return createAccumulation(member, point, constraints);
    }

    public void cancelAccumulation(Long accumulationId) {
        pointAccumulationRepository.findById(accumulationId)
                .orElseThrow(() -> new IllegalStateException("적립이력이 존재하지 않습니다."))
                .cancel();
    }

    public PointUsage use(String memberId, PointUsage usage) {
        List<PointAccumulation> accumulations = findAccumulationsByMemberId(memberId);
        usage.use(accumulations);
        pointUsageRepository.save(usage);
        return usage;
    }

    public void cancelUsage(Long usageId, Long totalPointToBeReversed) {
        List<PointAccumulation> reaccumulations = pointUsageRepository.findToCancel(usageId)
                .orElseThrow(() -> new IllegalStateException("사용이력이 존재하지 않습니다."))
                .cancelAndReturnReaccumulations(totalPointToBeReversed);

        if (!reaccumulations.isEmpty()) {
            pointAccumulationRepository.saveAll(reaccumulations);
        }
    }

    private PointAccumulation createAccumulation(Member member, Long point, AvailablePointConstraints constraints) {
        var accumulation = PointAccumulation.of(member, point,
                Objects.requireNonNullElseGet(constraints, AvailablePointConstraints::new));
        return pointAccumulationRepository.save(accumulation);
    }

    private List<PointAccumulation> findAccumulationsByMemberId(String memberId) {
        Member member = memberService.getMember(memberId);
        return pointAccumulationRepository.findToBeUsed(member);
    }
}
