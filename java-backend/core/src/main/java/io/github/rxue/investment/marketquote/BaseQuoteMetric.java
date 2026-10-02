package io.github.rxue.investment.marketquote;


public enum BaseQuoteMetric implements QuoteMetric {
    REGULAR_MARKET_PRICE("Regular Market Price"),
    CURRENCY("Currency"),
    REGULAR_MARKET_TIME("Regular Market Time"),
    REGULAR_MARKET_CHANGE("Regular Market Change"),
    REGULAR_MARKET_CHANGE_PERCENT("Regular Market Change Percent"),
    DIVIDEND_YIELD("Dividend Yield");
    private final String label;
    BaseQuoteMetric(String label) {
        this.label = label;
    }

    @Override
    public String label() {
        return label;
    }
}
