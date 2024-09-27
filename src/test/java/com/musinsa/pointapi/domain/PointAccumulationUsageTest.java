package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.GivenUtils;
import net.jqwik.api.Arbitraries;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.musinsa.pointapi.domain.vo.PointStatus.CANCELED;
import static com.musinsa.pointapi.domain.vo.PointStatus.PARTIALLY_USED;
import static org.junit.jupiter.api.Assertions.*;

class PointAccumulationUsageTest {

    @Test
    void 사용정보를_주입하면_사용정보에도_적립사용정보가_저장된다() {
        //given
        PointUsage usage = GivenUtils.create(PointUsage.class)
                .set("accumulationUsages", new ArrayList<>())
                .sample();
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .setNull("usage")
                .sample();

        //when
        accumulationUsage.setUsage(usage);

        //then
        Assertions.assertThat(usage.getAccumulationUsages().getFirst()).isEqualTo(accumulationUsage);
    }

    @Test
    void 포인트를_사용할_때_적립_리스트를_기준으로_분할한다() {
        //given
        long[] totalPointToBeUsed = {10L, 30L, 100L};
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .set("accumulation.currentPoint", 30L)
                .sample();

        //when
        long partiallyUsedPoint1 = accumulationUsage.calculatePartiallyUsedPoint(totalPointToBeUsed[0]);
        long partiallyUsedPoint2 = accumulationUsage.calculatePartiallyUsedPoint(totalPointToBeUsed[1]);
        long partiallyUsedPoint3 = accumulationUsage.calculatePartiallyUsedPoint(totalPointToBeUsed[2]);

        //then
        assertAll(
                () -> Assertions.assertThat(partiallyUsedPoint1).isEqualTo(10L),
                () -> Assertions.assertThat(partiallyUsedPoint2).isEqualTo(30L),
                () -> Assertions.assertThat(partiallyUsedPoint3).isEqualTo(30L)
        );
    }

    @Test
    void 포인트를_사용을_취소_할_때_적립사용의_포인트를_기준으로_분할한다() {
        //given
        long[] totalPointToBeReversed = {10L, 30L, 100L};
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .set("partiallyUsedPoint", 30L)
                .sample();

        //when
        long partiallyReversedPoint1 = accumulationUsage.calculatePartiallyReversedPoint(totalPointToBeReversed[0]);
        long partiallyReversedPoint2 = accumulationUsage.calculatePartiallyReversedPoint(totalPointToBeReversed[1]);
        long partiallyReversedPoint3 = accumulationUsage.calculatePartiallyReversedPoint(totalPointToBeReversed[2]);

        //then
        assertAll(
                () -> Assertions.assertThat(partiallyReversedPoint1).isEqualTo(10L),
                () -> Assertions.assertThat(partiallyReversedPoint2).isEqualTo(30L),
                () -> Assertions.assertThat(partiallyReversedPoint3).isEqualTo(30L)
        );
    }

    @Test
    void 포인트를_사용하면_부분사용포인트와_원본부분사용포인트는_같다() {
        //given
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .setNotNull("accumulationUsages")
                .set("accumulation.currentPoint", Arbitraries.longs().between(0,9999999))
                .set("partiallyUsedPoint", Arbitraries.longs().between(0,9999999))
                .set("originPartiallyUsedPoint", Arbitraries.longs().between(0,9999999))
                .sample();

        //when
        accumulationUsage.useAccumulationPoint(30L);

        //then
        Assertions.assertThat(accumulationUsage.getPartiallyUsedPoint()).isEqualTo(accumulationUsage.getOriginPartiallyUsedPoint());
    }

    @Test
    void 적립포인트의_사용을_되돌리면_부분사용포인트와_상태가_변경된다() {
        //given
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .set("partiallyUsedPoint", 30L)
                .setNotNull("accumulation")
                .set("accumulation.currentPoint", Arbitraries.longs().between(0, 999999))
                .set("accumulation.originPoint", Arbitraries.longs().between(0, 999999))
                .sample();

        //when
        accumulationUsage.reverseAccumulationPoint(20L);

        //then
        assertAll(
                () -> Assertions.assertThat(accumulationUsage.getPartiallyUsedPoint()).isEqualTo(10L),
                () -> Assertions.assertThat(accumulationUsage.getStatus()).isEqualTo(PARTIALLY_USED)
        );
    }

    @Test
    void 부분사용포인트가_0이면_CANCELED상태이다() {
        //given
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .setNotNull("accumulationUsage")
                .set("accumulation.currentPoint", Arbitraries.longs().between(0, 999999))
                .set("accumulation.originPoint", Arbitraries.longs().between(0, 999999))
                .set("partiallyUsedPoint", 30L)
                .sample();

        //when
        accumulationUsage.reverseAccumulationPoint(30L);

        //then
        Assertions.assertThat(accumulationUsage.getStatus()).isEqualTo(CANCELED);
    }
}