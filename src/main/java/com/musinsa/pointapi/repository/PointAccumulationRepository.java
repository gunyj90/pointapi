package com.musinsa.pointapi.repository;

import com.musinsa.pointapi.domain.PointAccumulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointAccumulationRepository extends JpaRepository<PointAccumulation, Long> {

    Optional<PointAccumulation> findByMemberAnd

}
