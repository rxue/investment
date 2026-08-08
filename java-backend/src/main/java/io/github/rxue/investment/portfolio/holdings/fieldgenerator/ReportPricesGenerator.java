package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Price;

import java.time.LocalDate;

public class ReportPricesGenerator extends AbstractFieldValuesGenerator<Price> {
    private final LocalDate date;
    private final MarketQuoteFetcher marketQuoteFetcher;
    public ReportPricesGenerator(LocalDate date, MarketQuoteFetcher marketQuoteFetcher) {
        this.date = date;
        this.marketQuoteFetcher = marketQuoteFetcher;
    }


    @Override
    Price getSingleValue(String companyId) {
        return date == null ? marketQuoteFetcher.getCurrentPriceInEuro(companyId)
                : marketQuoteFetcher.getClosePriceInEuro(companyId, date);
    }
}
