package io.github.rxue.investment.portfolio.twr.output;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.HoldingBuilderDirector;
import io.github.rxue.investment.portfolio.holdings.OptionalField;
import io.github.rxue.investment.vo.Util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PortfolioRawSnapshot {
    private final LocalDate date;
    private final BigDecimal cashInEuro;
    private final Map<String, List<Lot.Buy>> unrealizedLots;
    private final HoldingBuilderDirector hodlingBuilderDirector;
    public PortfolioRawSnapshot(LocalDate date, BigDecimal cashInEuro, Map<String,List<Lot.Buy>> unrealizedLots, MarketQuoteFetcher marketQuoteFetcher) {
        this.date = date;
        this.cashInEuro = cashInEuro;
        this.unrealizedLots = unrealizedLots;
        this.hodlingBuilderDirector = new HoldingBuilderDirector(List.of(OptionalField.REPORT_MARKET_VALUE), date, marketQuoteFetcher);
    }

    public BigDecimal getCashInEuro() {
        return cashInEuro;
    }

    public Map<String, List<Lot.Buy>> getUnrealizedLots() {
        return unrealizedLots;
    }

    public PortfolioSnapshot toSnapshot() {
        List<Holding> holdings = unrealizedLots.entrySet().stream()
                .map(hodlingBuilderDirector::direct)
                .map(Holding.Builder::build)
                .toList();
        return new PortfolioSnapshot(date, Util.toValueInCent(cashInEuro), holdings);
    }

}
