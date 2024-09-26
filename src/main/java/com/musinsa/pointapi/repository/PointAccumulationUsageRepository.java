package com.musinsa.pointapi.repository;

import com.musinsa.pointapi.domain.PointAccumulationUsage;
import com.musinsa.pointapi.domain.PointUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointAccumulationUsageRepository extends JpaRepository<PointAccumulationUsage, Long> {

    List<PointAccumulationUsage> findByUsage(PointUsage usage);
}
