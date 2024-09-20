package com.musinsa.pointapi.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import java.time.LocalDate;

@Embeddable
@Getter
public class AvailablePointDuration {

    private static final long DURATION_DAYS = 365L;

    private LocalDate startDate;
    private LocalDate endDate;

    public AvailablePointDuration() {
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(DURATION_DAYS);
    }

    public AvailablePointDuration from(AvailablePointConstraints constraints) {
        this.endDate = this.startDate.plusDays(constraints.getDuration());
        return this;
    }
}
