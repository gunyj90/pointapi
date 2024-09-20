package com.musinsa.pointapi.presentation;

import com.musinsa.pointapi.dto.PointAccumulationRequest;
import com.musinsa.pointapi.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Point관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 적립 API", description = "회원ID와 적립금액을 받아 포인트를 적립하는 API 입니다.")
    @PostMapping("/members/{memberId}/point/accumulation")
    public Long accumulate(@PathVariable(name = "memberId") String memberId,
                             @RequestBody PointAccumulationRequest request) {

        return pointService.accumulate(memberId, request.toEntity());
    }
}
