package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.PointStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointUsage extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accumulation_id")
    private PointAccumulation accumulation;

    private Long usedPoint;

    private LocalDateTime usedDateTime;

    @Enumerated(EnumType.STRING)
    private PointStatus status;
}
