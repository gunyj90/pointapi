package com.musinsa.pointapi.repository;

import com.musinsa.pointapi.domain.PointUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointUsageRepository extends JpaRepository<PointUsage, Long> {

    @Query("select u from PointUsage u " +
            "join fetch u.accumulationUsages au " +
            "join fetch au.accumulation a " +
            "join fetch a.member " +
            "where u.id = :id " +
            "and au.status in (com.musinsa.pointapi.domain.vo.PointStatus.USED, com.musinsa.pointapi.domain.vo.PointStatus.PARTIALLY_USED)")
    Optional<PointUsage> findToCancel(Long id);
}
