package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.marketquote.EuroPriceFetcher;
import io.github.rxue.investment.marketquote.Price;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatchResult;

import java.util.Arrays;

class HoldingGenerator {
    private final EuroPriceFetcher euroPriceFetcher;

    private HoldingGenerator(EuroPriceFetcher euroPriceFetcher) {
        this.euroPriceFetcher = euroPriceFetcher;
    }
    public HoldingGenerator() {
        this(new EuroPriceFetcher());
    }
    Holding generate(TradeLotsMatchResult tradeLotsMatchResult, Field... fields) {
        String companySymbol = tradeLotsMatchResult.securityId();
        Holding.Builder builder = new Holding.Builder(companySymbol, tradeLotsMatchResult.unrealizedShareAmount());
        if (Arrays.asList(fields).contains(Field.EURO_PRICE)) {
            Price priceInEuro = euroPriceFetcher.getCurrentEuroPrice(companySymbol);
            builder.setPriceInEuro(priceInEuro);
        }
        return builder.build();
    }
}
