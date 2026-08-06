package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.List;

public interface HoldingsGenerator {
    List<Holding> generate(List<Trade> trades, Fields fields);
}
