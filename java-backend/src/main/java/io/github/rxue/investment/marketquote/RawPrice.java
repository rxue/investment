package io.github.rxue.investment.marketquote;

/**
 * Merely a price value wrapper without any awareness of domain knowledge
 * @param value
 * @param currency
 * @param timestamp
 */
record RawPrice(double value, String currency, long timestamp) {
}
