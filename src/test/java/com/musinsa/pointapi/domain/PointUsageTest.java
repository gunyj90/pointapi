package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.GivenUtils;
import com.musinsa.pointapi.domain.vo.AvailablePointDuration;
import com.musinsa.pointapi.domain.vo.PointStatus;
import net.jqwik.api.Arbitraries;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PointUsageTest {

    @Test
    void 포인트사용데이터를_생성하면_사용일자는_오늘일자이고_상태는_USED이다() {
        //given
        //when
        PointUsage pointUsage = PointUsage.of("abc", 100L);

        //then
        assertAll(
                () -> Assertions.assertThat(pointUsage.getUsedDateTime().getYear()).isEqualTo(LocalDateTime.now().getYear()),
                () -> Assertions.assertThat(pointUsage.getUsedDateTime().getMonthValue()).isEqualTo(LocalDateTime.now().getMonthValue()),
                () -> Assertions.assertThat(pointUsage.getUsedDateTime().getDayOfMonth()).isEqualTo(LocalDateTime.now().getDayOfMonth()),
                () -> Assertions.assertThat(pointUsage.getStatus()).isEqualTo(PointStatus.USED)
        );
    }

    @Test
    void 사용정보에_적립사용데이터를_넣으면_적립사용은_사용정보를_포함한다() {
        //given
        PointUsage pointUsage = GivenUtils.create(PointUsage.class).sample();
        PointAccumulationUsage accumulationUsage = GivenUtils.create(PointAccumulationUsage.class)
                .setNull("usage")
                .sample();

        //when
        pointUsage.addAccumulationUsage(accumulationUsage);

        //then
        Assertions.assertThat(accumulationUsage.getUsage()).isEqualTo(pointUsage);
    }

    @Test
    void 사용취소시_실제_사용된_포인트보다_큰_금액을_취소할_수_없다() {
        //given
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .sample();

        //when
        //then
        Assertions.assertThatThrownBy(() -> pointUsage.cancelAndReturnReaccumulations(20L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용취소시_사용포인트가_줄어들고_상태가_변경된다() {
        //given
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .size("accumulationUsages", 2)
                .set("accumulationUsages[0].partiallyUsedPoint", 3L)
                .set("accumulationUsages[0].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[0].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .set("accumulationUsages[1].partiallyUsedPoint", 7L)
                .set("accumulationUsages[1].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[1].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .sample();

        //when
        pointUsage.cancelAndReturnReaccumulations(3L);

        //then
        assertAll(
                () -> Assertions.assertThat(pointUsage.getUsedPoint()).isEqualTo(7L),
                () -> Assertions.assertThat(pointUsage.getStatus()).isEqualTo(PointStatus.PARTIALLY_USED)
        );
    }

    @Test
    void 사용취소시_사용포인트가_모두_최소되면_CANCELD상태로_변경된다() {
        //given
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .size("accumulationUsages", 2)
                .set("accumulationUsages[0].partiallyUsedPoint", 3L)
                .set("accumulationUsages[0].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[0].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .set("accumulationUsages[1].partiallyUsedPoint", 7L)
                .set("accumulationUsages[1].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[1].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .sample();

        //when
        pointUsage.cancelAndReturnReaccumulations(10L);

        //then
        Assertions.assertThat(pointUsage.getStatus()).isEqualTo(PointStatus.CANCELED);
    }

    @Test
    void 사용취소시_만료된_적립건을_대상으로_재적립_데이터를_반환한다() {
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .size("accumulationUsages", 2)
                .set("accumulationUsages[0].partiallyUsedPoint", 3L)
                .set("accumulationUsages[0].accumulation.duration.startDate", LocalDateTime.now().minusDays(2L))
                .set("accumulationUsages[0].accumulation.duration.endDate", LocalDateTime.now().minusDays(1L))
                .set("accumulationUsages[0].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .set("accumulationUsages[1].partiallyUsedPoint", 7L)
                .set("accumulationUsages[1].accumulation.duration.startDate", LocalDateTime.now().minusDays(1L))
                .set("accumulationUsages[1].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[1].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .sample();

        //when
        List<PointAccumulation> reaccumulations = pointUsage.cancelAndReturnReaccumulations(10L);

        //then
        Assertions.assertThat(reaccumulations.size()).isEqualTo(1);
    }

    @Test
    void 재적립_대상은_부분사용금액과_동일하고_유효일수도_동일하다() {
        long 부분사용금액 = 3L;
        long 유효일수 = 2L;
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .size("accumulationUsages", 2)
                .set("accumulationUsages[0].partiallyUsedPoint", 부분사용금액)
                .set("accumulationUsages[0].accumulation.duration.startDate", LocalDateTime.now().minusDays(유효일수 + 1L))
                .set("accumulationUsages[0].accumulation.duration.endDate", LocalDateTime.now().minusDays(유효일수 - 1L))
                .set("accumulationUsages[0].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .set("accumulationUsages[1].partiallyUsedPoint", 7L)
                .set("accumulationUsages[1].accumulation.duration.startDate", LocalDateTime.now().minusDays(1L))
                .set("accumulationUsages[1].accumulation.duration.endDate", LocalDateTime.now().plusDays(1L))
                .set("accumulationUsages[1].accumulation.currentPoint", Arbitraries.longs().between(1L, 10L))
                .sample();

        //when
        List<PointAccumulation> reaccumulations = pointUsage.cancelAndReturnReaccumulations(10L);

        //then
        assertAll(
                () -> {Assertions.assertThat(reaccumulations.getFirst().getCurrentPoint()).isEqualTo(부분사용금액);},
                () -> {Assertions.assertThat(reaccumulations.getFirst().getOriginPoint()).isEqualTo(부분사용금액);},
                () -> {
                    AvailablePointDuration duration = reaccumulations.getFirst().getDuration();
                    long dur = ChronoUnit.DAYS.between(duration.getStartDate(), duration.getEndDate());
                    Assertions.assertThat(dur).isEqualTo(유효일수);
                    }

        );

    }
}