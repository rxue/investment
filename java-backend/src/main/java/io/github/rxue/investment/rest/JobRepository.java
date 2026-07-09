package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<XIRRJob,Long> {
}
