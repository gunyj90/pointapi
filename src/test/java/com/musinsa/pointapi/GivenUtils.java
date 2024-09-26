package com.musinsa.pointapi;

import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.PointAccumulation;
import com.musinsa.pointapi.domain.vo.AvailablePointConstraints;
import com.musinsa.pointapi.domain.vo.MemberPointConstraints;
import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;

import java.util.Arrays;

public class GivenUtils {

    private GivenUtils(){}

    public static <T> ArbitraryBuilder<T> create(Class<T> clazz) {
        var sut = FixtureMonkey.builder()
                .objectIntrospector(new FailoverIntrospector(
                        Arrays.asList(
                                FieldReflectionArbitraryIntrospector.INSTANCE,
                                ConstructorPropertiesArbitraryIntrospector.INSTANCE
                        )
                ))
                .build();

        return sut.giveMeBuilder(clazz);
    }

    public static PointAccumulation pointAccumulation(Long days, Long point) {
        Member member =  GivenUtils.create(Member.class)
                .set("memberPointConstraints", new MemberPointConstraints())
                .sample();
        AvailablePointConstraints constraints =  GivenUtils.create(AvailablePointConstraints.class)
                .set("duration", days)
                .sample();
        return PointAccumulation.of(member, point, constraints);
    }
}
