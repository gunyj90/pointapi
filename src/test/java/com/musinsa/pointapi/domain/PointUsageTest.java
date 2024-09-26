package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.GivenUtils;
import com.musinsa.pointapi.domain.vo.PointStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        Assertions.assertThatThrownBy(() -> pointUsage.cancel(20L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용취소시_사용포인트가_줄어들고_상태가_변경된다() {
        //given
        PointUsage pointUsage = GivenUtils.create(PointUsage.class)
                .set("usedPoint", 10L)
                .sample();

        //when
        pointUsage.cancel(3L);

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
                .sample();

        //when
        pointUsage.cancel(10L);

        //then
        Assertions.assertThat(pointUsage.getStatus()).isEqualTo(PointStatus.CANCELED);
    }
}