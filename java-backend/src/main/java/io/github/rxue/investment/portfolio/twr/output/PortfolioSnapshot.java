package io.github.rxue.investment.portfolio.twr.output;

import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.OptionalField;
import io.github.rxue.investment.portfolio.money.Util;

import java.time.LocalDate;
import java.util.List;

public record PortfolioSnapshot(LocalDate date, long cashInEuroCent, List<Holding> holdings) {
    long valueInEuroCent() {
        return cashInEuroCent + holdings.stream()
                .mapToLong(holding -> Util.toValueInCent(holding.value(OptionalField.MARKET_VALUE_IN_EURO)))
                .sum();
    }
}
