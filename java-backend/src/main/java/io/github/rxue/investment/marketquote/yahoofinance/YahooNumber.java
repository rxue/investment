package io.github.rxue.investment.marketquote.yahoofinance;

import java.math.BigDecimal;

public record YahooNumber(BigDecimal raw, String formatedValue) {
    @Override
    public String toString() {
        return formatedValue;
    }
}
