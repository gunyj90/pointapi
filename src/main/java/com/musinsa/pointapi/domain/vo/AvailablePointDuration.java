package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailablePointDuration {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public AvailablePointDuration(AvailablePointConstraints constraints) {
        this.startDate = LocalDateTime.now();
        this.endDate = this.startDate.plusDays(constraints.getDuration());
    }
}
