package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailablePointDuration {

    private LocalDate startDate;
    private LocalDate endDate;

    public AvailablePointDuration(AvailablePointConstraints constraints) {
        this.startDate = LocalDate.now();
        this.endDate = this.startDate.plusDays(constraints.getDuration());
    }
}
