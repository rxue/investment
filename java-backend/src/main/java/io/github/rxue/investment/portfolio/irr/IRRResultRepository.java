package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.portfolio.irr.jpaentity.IRRJob;
import io.github.rxue.investment.portfolio.irr.jpaentity.IRRResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRRResultRepository extends JpaRepository<IRRResult,Long> {
    Optional<IRRResult> findByJob(IRRJob job);
}
