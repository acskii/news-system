package com.acskii.common.repos;

import com.acskii.common.models.Analytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticRepository extends JpaRepository<Analytic, Long> {
    Optional<Analytic> findFirstByOrderByAnalysedAtDesc();
}
