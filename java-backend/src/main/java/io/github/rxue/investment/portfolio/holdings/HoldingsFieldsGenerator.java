package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.portfolio.holdings.fieldgenerator.FieldValuesGenerator;
import io.github.rxue.investment.portfolio.holdings.fieldgenerator.PortfolioWeightsGenerator;
import io.github.rxue.investment.portfolio.holdings.fieldgenerator.PricesGenerator;
import io.github.rxue.investment.portfolio.holdings.fieldgenerator.ReportMarketValuesGenerator;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class HoldingsFieldsGenerator implements HoldingsGenerator {
    private final TradeLotsMatcher tradeLotsMatcher;
    private final LocalDate date;
    private final MarketQuoteFetcher marketQuoteFetcher;
    public HoldingsFieldsGenerator(TradeLotsMatcher tradeLotsMatcher, MarketQuoteFetcher marketQuoteFetcher, LocalDate date) {
        this.tradeLotsMatcher = tradeLotsMatcher;
        this.date = date;
        this.marketQuoteFetcher = marketQuoteFetcher;
    }

    @Override
    public List<Holding> generate(List<Trade> trades, Fields fields) {
        Map<String,Integer> positions = tradeLotsMatcher.matchInFifo(trades, Map.of())
                .getPositions();
        List<String> companyIds = List.copyOf(positions.keySet());
        List<Holding.Builder> holdingBuilders = initHoldingBuilders(positions);
        Map<String,BigDecimal> reportMarketValues = Map.of();
        for (OptionalField optionalField : fields.optionalFields()) {
            Map<String,Object> values = null;
            switch(optionalField) {
                case OptionalField.PRICE -> {
                    values = new PricesGenerator(date, marketQuoteFetcher)
                            .generate(companyIds);
                }
                case OptionalField.REPORT_MARKET_VALUE -> {
                    reportMarketValues = new ReportMarketValuesGenerator(date, marketQuoteFetcher, positions)
                            .generateGeneric(companyIds);
                    values = FieldValuesGenerator.toNonGeneric(reportMarketValues);
                }
                case OptionalField.PORTFOLIO_WEIGHT -> {
                    values = new PortfolioWeightsGenerator(reportMarketValues,
                            new ReportMarketValuesGenerator(date, marketQuoteFetcher, positions))
                            .generate(companyIds);
                }
            }
            set(holdingBuilders, optionalField, values);
        }
        return holdingBuilders.stream()
                .map(Holding.Builder::build)
                .toList();
    }

    private static List<Holding.Builder> initHoldingBuilders(Map<String,Integer> positions) {
        List<Holding.Builder> holdingBuilders = new ArrayList<>();
        for (Map.Entry<String,Integer> entry : positions.entrySet()) {
            Holding.Builder builder = new Holding.Builder(entry.getKey(), entry.getValue());
            holdingBuilders.add(builder);
        }
        return holdingBuilders;
    }
    private static void set(List<Holding.Builder> holdingBuilders, OptionalField field, Map<String,Object> values) {
        for (Holding.Builder holdingBuilder : holdingBuilders) {
            String companyId = holdingBuilder.value(CompulsoryField.COMPANY_ID);
            holdingBuilder.set(field, values.get(companyId));
        }
    }
}
