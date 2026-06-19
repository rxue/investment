package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.XIRRRawInput;
import org.springframework.data.jpa.repository.JpaRepository;

interface RawInputRepository extends JpaRepository<XIRRRawInput,Long> {
}
