package com.musinsa.pointapi.presentation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse extends RepresentationModel<ResultResponse> {

    private final String code;
    private final String message;

    private static final String SUCCESS_CODE = "200";
    private static final String FAILURE_CODE = "500";
    private static final String SUCCESS_MESSAGE = "성공적으로 처리되었습니다.";
    private static final String FAILURE_MESSAGE = "처리되지 않았습니다.";

    public static ResultResponse success() {
        return new ResultResponse(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static ResultResponse failure() {
        return new ResultResponse(FAILURE_CODE, FAILURE_MESSAGE);
    }

    public static ResultResponse failure(String message) {
        return new ResultResponse(FAILURE_CODE, message);
    }
}
