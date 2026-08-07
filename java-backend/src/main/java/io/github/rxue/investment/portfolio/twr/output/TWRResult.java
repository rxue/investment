package io.github.rxue.investment.portfolio.twr.output;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public record TWRResult(List<SubPeriodPortfolioSnapshots> subPeriodPortfoliosSnapshotsList) {
    public double value() {
        double[] returns = subPeriodPortfoliosSnapshotsList.stream()
                .mapToDouble(subPeriodSnapshots -> {
                    Pair<Long,Long> values = subPeriodSnapshots.finalInput();
                    return (double) values.getRight() / values.getLeft() - 1;
                }).toArray();
        return Arrays.stream(returns)
                .map(r -> 1 + r)
                .reduce(1, (a, b) -> a * b) - 1;
    }
}
