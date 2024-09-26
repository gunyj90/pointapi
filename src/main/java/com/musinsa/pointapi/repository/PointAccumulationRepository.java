package com.musinsa.pointapi.repository;

import com.musinsa.pointapi.domain.Member;
import com.musinsa.pointapi.domain.PointAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointAccumulationRepository extends JpaRepository<PointAccumulation, Long> {

    @Query("select a from PointAccumulation a " +
            "where a.member = :member " +
            "and a.status in (com.musinsa.pointapi.domain.vo.PointStatus.ACCUMULATED, com.musinsa.pointapi.domain.vo.PointStatus.PARTIALLY_USED) " +
            "and current_timestamp between a.duration.startDate and a.duration.endDate " +
            "order by a.accumulationType, a.duration.endDate")
    List<PointAccumulation> findToBeUsed(@Param("member") Member member);
}
