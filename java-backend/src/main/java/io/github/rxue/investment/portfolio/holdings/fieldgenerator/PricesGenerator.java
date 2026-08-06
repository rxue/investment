package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Price;

import java.time.LocalDate;

public class PricesGenerator extends AbstractFieldValuesGenerator<Price> {
    private final LocalDate date;
    private final MarketQuoteFetcher marketQuoteFetcher;
    public PricesGenerator(LocalDate date, MarketQuoteFetcher marketQuoteFetcher) {
        this.date = date;
        this.marketQuoteFetcher = marketQuoteFetcher;
    }


    @Override
    Price getSingleValue(String companyId) {
        return date == null ? marketQuoteFetcher.getCurrentPrice(companyId)
                : marketQuoteFetcher.getClosePrice(companyId, date);
    }
}
