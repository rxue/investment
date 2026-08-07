package io.github.rxue.investment.portfolio.snapshot;

import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.OptionalField;
import io.github.rxue.investment.vo.Util;

import java.time.LocalDate;
import java.util.List;

public record PortfolioSnapshot(LocalDate date, long cashInEuroCent, List<Holding> holdings) {
    public long valueInEuroCent() {
        return cashInEuroCent + holdings.stream()
                .mapToLong(holding -> Util.toValueInCent(holding.value(OptionalField.REPORT_MARKET_VALUE)))
                .sum();
    }
}
