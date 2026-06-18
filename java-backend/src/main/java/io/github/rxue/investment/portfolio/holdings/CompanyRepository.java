package io.github.rxue.investment.portfolio.holdings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Integer> {
    Optional<Company> findByOpSymbol(String yahooSymbol);
}
