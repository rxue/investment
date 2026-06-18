package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.portfolio.irr.jpaentity.IRRJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<IRRJob,Long> {
}
