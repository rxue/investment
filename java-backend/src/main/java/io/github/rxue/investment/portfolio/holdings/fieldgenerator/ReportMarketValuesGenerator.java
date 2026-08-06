package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

public class ReportMarketValuesGenerator extends AbstractFieldValuesGenerator<BigDecimal> {
    private final LocalDate date;
    private final MarketQuoteFetcher marketQuoteFetcher;
    private final Map<String,Integer> positions;
    public ReportMarketValuesGenerator(LocalDate date, MarketQuoteFetcher marketQuoteFetcher,Map<String,Integer> positions) {
        this.date = date;
        this.marketQuoteFetcher = marketQuoteFetcher;
        this.positions = positions;
    }
    @Override
    BigDecimal getSingleValue(String companyId) {
        Price price = date == null ? marketQuoteFetcher.getCurrentPriceInEuro(companyId) : marketQuoteFetcher.getClosePriceInEuro(companyId, date);
        return price.value()
                .multiply(BigDecimal.valueOf(positions.get(companyId)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
