package com.musinsa.pointapi.presentation;

import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.PointUsage;
import com.musinsa.pointapi.presentation.dto.*;
import com.musinsa.pointapi.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Point관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 적립 API", description = "회원ID와 적립금액을 받아 포인트를 적립하는 API 입니다.")
    @PostMapping("/members/{memberId}/point/accumulation")
    public EntityModel<PointAccumulationResponse> accumulate(@PathVariable(name = "memberId") String memberId,
                                                            @RequestBody PointAccumulationRequest request) {
        PointAccumulation accumulation = pointService.accumulate(memberId, request.point(), request.constraints());
        return EntityModel.of(PointAccumulationResponse.from(accumulation),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).accumulate(memberId, request)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).cancelAccumulationById(accumulation.getId())).withRel("accumulation"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).use(memberId, new PointUsageRequest("orderId", accumulation.getCurrentPoint()))).withRel("usage")
        );
    }

    @Operation(summary = "포인트 적립취소 API", description = "적립ID를 받아 적립을 취소하는 API 입니다.")
    @DeleteMapping("/point/accumulations/{accumulationId}")
    public EntityModel<ResultResponse> cancelAccumulationById(@PathVariable(name = "accumulationId") Long accumulationId) {
        pointService.cancelAccumulation(accumulationId);
        return EntityModel.of(ResultResponse.success(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).cancelAccumulationById(accumulationId)).withSelfRel()
        );
    }

    @Operation(summary = "포인트 사용 API", description = "회원ID와 금액을 받아 포인트를 사용하는 API 입니다.")
    @PostMapping("/members/{memberId}/point/usage")
    public EntityModel<PointUsageResponse> use(@PathVariable(name = "memberId") String memberId,
                                  @RequestBody PointUsageRequest request) {
        PointUsage usage = pointService.use(memberId, request.toEntity());
        return EntityModel.of(PointUsageResponse.from(usage),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).use(memberId, request)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).cancelUsageById(usage.getId(),new CancelRequest(usage.getUsedPoint()))).withRel("usage")
        );
    }

    @Operation(summary = "포인트 사용취소 API", description = "사용ID를 받아 사용을 취소하는 API 입니다.")
    @PatchMapping("/point/usages/{usageId}")
    public EntityModel<ResultResponse> cancelUsageById(@PathVariable(name = "usageId") Long usageId,
                                          @RequestBody CancelRequest request) {
        pointService.cancelUsage(usageId, request.point());
        return EntityModel.of(ResultResponse.success(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).cancelUsageById(usageId,request)).withSelfRel()
        );
    }
}
