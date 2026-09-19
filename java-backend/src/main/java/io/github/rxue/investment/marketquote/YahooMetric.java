package io.github.rxue.investment.marketquote;

public enum YahooMetric {
    CURRENCY("currency", "price"),
    REGULAR_MARKET_PRICE("regularMarketPrice", "price"),
    REGULAR_MARKET_TIME("regularMarketTime", "price"),
    TRAILING_PE("trailingPE","summaryDetail");
    private String name;
    private String module;
    YahooMetric(String name, String v10Module) {
        this.name = name;
        this.module = v10Module;
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }


}
