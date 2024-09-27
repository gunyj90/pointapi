package com.musinsa.pointapi.domain;

import com.musinsa.pointapi.domain.vo.MemberPointConstraints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void 최대적립포인트는_항상_커야하고_1회_적립가능포인트는_min값이_항상_max보다_작다() {
        //given
        long 최대적립포인트가_작은_경우 = 2L;
        long 적립가능max가_min보다_작은경우 = 1L;

        //when
        //then
        assertAll(
                () -> Assertions.assertThatException().isThrownBy(() -> {
                    //최대적립포인트는_항상_커야
                    Member.builder().memberPointConstraints(
                            new MemberPointConstraints(최대적립포인트가_작은_경우, 1L, 999L)
                            );
                }).isInstanceOf(IllegalStateException.class),
                //1회_적립가능포인트는_min값이_항상_max보다_작다
                () -> Assertions.assertThatException().isThrownBy(() -> {
                    Member.builder().memberPointConstraints(
                            new MemberPointConstraints(999_999L,999L, 적립가능max가_min보다_작은경우)
                    );
                }).isInstanceOf(IllegalStateException.class)
        );
    }
}