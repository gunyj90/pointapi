package com.musinsa.pointapi.presentation;

import com.musinsa.pointapi.presentation.dto.MemberCreationRequest;
import com.musinsa.pointapi.presentation.dto.MemberModifyRequest;
import com.musinsa.pointapi.presentation.dto.ResultResponse;
import com.musinsa.pointapi.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 생성 API", description = "회원정보를 받아 이를 생성하는 API 입니다.")
    @PostMapping("/member")
    public EntityModel<ResultResponse> createMember(@RequestBody MemberCreationRequest request) {
        memberService.create(request.toEntity());
        return EntityModel.of(ResultResponse.success(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).createMember(request)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).modifyMember(request.memberId(),
                        new MemberModifyRequest(request.name(), request.constraintRequest()))).withRel("member"));
    }

    @Operation(summary = "회원 변경 API", description = "회원이름, 최대 소유 포인트, 1회 최소적립 포인트, 1회 최대적립 포인트를 받아 변경하는 API 입니다.")
    @PutMapping("/members/{memberId}")
    public EntityModel<ResultResponse> modifyMember(@PathVariable(name = "memberId") String memberId,
                                       @RequestBody MemberModifyRequest request) {
        memberService.modify(memberId, request.name(), request.constraintRequest().toMemberPointConstraints());
        return EntityModel.of(ResultResponse.success(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).modifyMember(memberId, request)).withSelfRel());
    }
}
