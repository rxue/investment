package io.github.rxue.investment.portfolio.twr.output;

import io.github.rxue.investment.portfolio.snapshot.PortfolioSnapshot;
import org.apache.commons.lang3.tuple.Pair;

public record SubPeriodPortfolioSnapshots(PortfolioSnapshot startSnapshot, PortfolioSnapshot endSnapshot) {
    Pair<Long,Long> finalInput() {
       return Pair.of(startSnapshot.liquidityInEuroCent(), endSnapshot().liquidityInEuroCent());
    }
}
