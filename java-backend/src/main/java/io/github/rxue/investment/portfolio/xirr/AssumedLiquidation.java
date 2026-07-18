package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.Util;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.OptionalField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Assumed eventually all the money is withdrawn on the calculation date
 * @param holdings
 * @param remainingCash
 */
record AssumedLiquidation(List<Holding> holdings, BigDecimal remainingCash) {
    CashFlowInput toCashFlowInput() {
        long totalMarketValueInEuroCent = holdings.stream()
                .mapToLong(h -> Util.toValueInCent(h.value(OptionalField.MARKET_VALUE_IN_EURO)))
                .sum() + Util.toValueInCent(remainingCash);
        return new CashFlowInput(LocalDate.now(), CashFlowType.ASSUMED_LIQUIDATION, -totalMarketValueInEuroCent);
    }
}
