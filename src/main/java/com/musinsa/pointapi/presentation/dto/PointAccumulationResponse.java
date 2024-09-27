package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.PointAccumulation;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointAccumulationResponse extends RepresentationModel<PointAccumulationResponse> {

    private final Long accumulationId;
    private final Long point;

    public static PointAccumulationResponse from(PointAccumulation accumulation) {
        return PointAccumulationResponse.builder()
                .accumulationId(accumulation.getId())
                .point(accumulation.getOriginPoint())
                .build();
    }
}
