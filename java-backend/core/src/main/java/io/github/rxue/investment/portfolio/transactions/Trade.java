package io.github.rxue.investment.portfolio.transactions;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @param tickerSymbol
 * @param shareAmount this is an unsigned int, i.e. always positive
 * @param type
 * @param cents
 */
public record Trade(String tickerSymbol, LocalDate date, int shareAmount, Type type, long cents) implements Transaction {
    public Trade {
        Objects.requireNonNull(tickerSymbol, "tickerSymbol");
        Objects.requireNonNull(type, "type");
        if (shareAmount <= 0) {
            throw new IllegalArgumentException("shareAmount must be positive: " + shareAmount);
        }
    }

    public enum Type {
        BUY, SELL
    }
}
