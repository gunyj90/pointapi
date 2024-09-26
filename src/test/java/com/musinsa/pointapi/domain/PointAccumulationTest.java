package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.GivenUtils;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.musinsa.pointapi.domain.vo.PointStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class PointAccumulationTest {

    @Test
    void 제약조건파라메터에_따라_적립유효일이_결정된다() {
        //given
        PointAccumulation accumulation = GivenUtils.pointAccumulation(30L, 100L);

        //when
        long days = ChronoUnit.DAYS.between(accumulation.getDuration().getStartDate(), accumulation.getDuration().getEndDate());

        //then
        Assertions.assertThat(days).isEqualTo(30L);
    }

    @Test
    void 사용자에_설정된_1회_적립가능_포인트를_초과하면_적립이_불가하다() {
        //given
        Member member = GivenUtils.create(Member.class)
                .set("memberPointConstraints.minAccumulatedPointAtOnce", 1L)
                .set("memberPointConstraints.maxAccumulatedPointAtOnce", 10L)
                .sample();

        //when
        //then
        Assertions.assertThatThrownBy(() -> PointAccumulation.of(member, 100L, new AvailablePointConstraints()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 만료된_적립을_갱신했을때_두_적립의_기간일은_동일하다() {
        //given
        PointAccumulation accumulation = GivenUtils.pointAccumulation(30L, 100L);

        //when
        PointAccumulation renewed = PointAccumulation.renew(accumulation, 50L);
        long oldAccDays = ChronoUnit.DAYS.between(accumulation.getDuration().getStartDate(), accumulation.getDuration().getEndDate());
        long newAccDays = ChronoUnit.DAYS.between(renewed.getDuration().getStartDate(), renewed.getDuration().getEndDate());

        //then
        Assertions.assertThat(oldAccDays).isEqualTo(newAccDays);
    }

    @Test
    void 적립상태가_아니면_적립취소를_할_수_없다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("status", CANCELED)
                .sample();

        //when
        //then
        Assertions.assertThatThrownBy(accumulation::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 유효종료일자가_오늘이전이라면_만료된_것이다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("duration.endDate", LocalDateTime.now().minusDays(1))
                .sample();

        //when
        boolean expired = accumulation.isExpired();

        //then
        Assertions.assertThat(expired).isTrue();
    }

    @Test
    void 적립된_포인트를_줄였을떄_포인트와_상태가_변한다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("currentPoint", 100L)
                .set("status", ACCUMULATED)
                .sample();

        //when
        accumulation.reduceCurrentPoint(55L);

        //then
        assertAll(
                () -> Assertions.assertThat(accumulation.getCurrentPoint()).isEqualTo(45L),
                () -> Assertions.assertThat(accumulation.getStatus()).isEqualTo(PARTIALLY_USED)
        );
    }

    @Test
    void 적립된_포인트를_모두_사용하면_USED상태로_변경된다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("currentPoint", 100L)
                .set("status", ACCUMULATED)
                .sample();

        //when
        accumulation.reduceCurrentPoint(100L);

        //then
        Assertions.assertThat(accumulation.getStatus()).isEqualTo(USED);
    }

    @Test
    void 적립된_포인트를_증가시키면_포인트와_상태가_변경된다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("currentPoint", 0L)
                .set("originPoint", 100L)
                .set("status", USED)
                .sample();

        //when
        accumulation.increaseCurrentPoint(30L);

        //then
        assertAll(
                () -> Assertions.assertThat(accumulation.getCurrentPoint()).isEqualTo(30L),
                () -> Assertions.assertThat(accumulation.getStatus()).isEqualTo(PARTIALLY_USED)
        );
    }

    @Test
    void 적립된_포인트가_초기_적립포인트와_동일해지면_ACCUMULATED상태로_변경된다() {
        //given
        PointAccumulation accumulation = GivenUtils.create(PointAccumulation.class)
                .set("currentPoint", 0L)
                .set("originPoint", 100L)
                .set("status", USED)
                .sample();

        //when
        accumulation.increaseCurrentPoint(100L);

        //then
        Assertions.assertThat(accumulation.getStatus()).isEqualTo(ACCUMULATED);
    }

}