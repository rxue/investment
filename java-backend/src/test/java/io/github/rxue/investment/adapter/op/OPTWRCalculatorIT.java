package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.holdings.CompulsoryField;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.OptionalField;
import io.github.rxue.investment.portfolio.twr.TWRCalculator;
import io.github.rxue.investment.portfolio.twr.output.PortfolioSnapshot;
import io.github.rxue.investment.portfolio.twr.output.SubPeriodPortfolioSnapshots;
import io.github.rxue.investment.portfolio.twr.output.TWRResult;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OPTWRCalculatorIT {
    private final OPTWRCalculator out = new OPTWRCalculator(new TWRCalculator(), new OPTransactionExtractor());

    @Test
    void calculate_with_a_csv_containing_two_deposits_returns_exactly_two_sub_periods() {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("op_single_sub_period_transactions.csv");
        assertNotNull(csvInputStream, "test resource op_single_sub_period_transactions.csv must be on the classpath");

        TWRResult result = out.calculate(List.of(csvInputStream));

        List<SubPeriodPortfolioSnapshots> subPeriodSnapshots = result.subPeriodPortfoliosSnapshotsList();
        assertEquals(2, subPeriodSnapshots.size());

        assertEquivalent(new PortfolioSnapshot(LocalDate.of(2025, 7, 21), 7000*100L, List.of()), subPeriodSnapshots.get(0).startSnapshot());

        Holding.Builder pfeHoldingBuilder = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "PFE")
                .set(CompulsoryField.POSITION, 30);
        Holding pfeHolding1 = pfeHoldingBuilder.set(OptionalField.MARKET_VALUE_IN_EURO, BigDecimal.valueOf(637.16))
                .build();
        long expectedFirstSubPeriodEndCash = 7000*100-89999-62570;
        assertEquivalent(new PortfolioSnapshot(LocalDate.of(2025, 8, 31), expectedFirstSubPeriodEndCash, List.of(pfeHolding1)), subPeriodSnapshots.get(0).endSnapshot());
        //
        Holding pfeHolding2Start = pfeHoldingBuilder.set(OptionalField.MARKET_VALUE_IN_EURO, BigDecimal.valueOf(634.06))
                .build();
        long expectedSecondPeriodStartCash = expectedFirstSubPeriodEndCash + 8000*100;
        assertEquivalent(new PortfolioSnapshot(LocalDate.of(2025, 9, 1), expectedSecondPeriodStartCash, List.of(pfeHolding2Start)), subPeriodSnapshots.get(1).startSnapshot());
        Holding pfeHolding2End = pfeHoldingBuilder.set(OptionalField.MARKET_VALUE_IN_EURO, new BigDecimal("638.20"))
                .build();
        Holding stzHolding = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "STZ")
                .set(CompulsoryField.POSITION, 4)
                .set(OptionalField.MARKET_VALUE_IN_EURO, BigDecimal.valueOf(502.84))
                .build();
        assertEquivalent(new PortfolioSnapshot(LocalDate.of(2025, 9, 3), expectedSecondPeriodStartCash - 52566, List.of(stzHolding,pfeHolding2End)), subPeriodSnapshots.get(1).endSnapshot());

    }
    private void assertEquivalent(PortfolioSnapshot expected, PortfolioSnapshot actual) {
        assertEquals(expected.date(), actual.date());
        assertEquals(expected.cashInEuroCent(), actual.cashInEuroCent());
        assertEquals(sortedByCompanyId(expected.holdings()), sortedByCompanyId(actual.holdings()));
    }

    private static List<Holding> sortedByCompanyId(List<Holding> holdings) {
        return holdings.stream()
                .sorted(Comparator.comparing(holding -> holding.<String>value(CompulsoryField.COMPANY_ID)))
                .toList();
    }
}
