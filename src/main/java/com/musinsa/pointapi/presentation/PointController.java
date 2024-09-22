package com.musinsa.pointapi.presentation;

import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.dto.PointAccumulationCancelResponse;
import com.musinsa.pointapi.dto.PointAccumulationRequest;
import com.musinsa.pointapi.dto.PointAccumulationResponse;
import com.musinsa.pointapi.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Point관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 적립 API", description = "회원ID와 적립금액을 받아 포인트를 적립하는 API 입니다.")
    @PostMapping("/members/{memberId}/point/accumulation")
    public PointAccumulationResponse accumulate(@PathVariable(name = "memberId") String memberId,
                                                @RequestBody PointAccumulationRequest request) {
        PointAccumulation accumulation = pointService.accumulate(memberId, request.toEntity());
        return PointAccumulationResponse.from(accumulation);
    }

    @Operation(summary = "포인트 적립취소 API", description = "회원ID와 금액을 받아 포인트를 적립하는 API 입니다.")
    @DeleteMapping("/point/cancel/{accumulationId}")
    public PointAccumulationCancelResponse cancel(@PathVariable(name = "accumulationId") Long accumulationId) {
        Long canceledPoint = pointService.cancel(accumulationId);
        return new PointAccumulationCancelResponse(canceledPoint);
    }
}
