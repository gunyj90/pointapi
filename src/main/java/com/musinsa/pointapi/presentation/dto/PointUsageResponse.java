package com.musinsa.pointapi.presentation.dto;

import com.musinsa.pointapi.domain.PointUsage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointUsageResponse extends RepresentationModel<PointUsageResponse> {

    private final Long usageId;
    private final Long point;

    public static PointUsageResponse from(PointUsage usage) {
        return PointUsageResponse.builder()
                .usageId(usage.getId())
                .point(usage.getUsedPoint())
                .build();
    }
}
