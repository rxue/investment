package io.github.rxue.investment.marketquote.yahoofinance;

import java.math.BigDecimal;

public record YahooNumber(BigDecimal raw, String formatedValue) implements Comparable<YahooNumber> {
    @Override
    public String toString() {
        return formatedValue;
    }

    @Override
    public int compareTo(YahooNumber yahooNumber) {
        return raw.compareTo(yahooNumber.raw);
    }
}
