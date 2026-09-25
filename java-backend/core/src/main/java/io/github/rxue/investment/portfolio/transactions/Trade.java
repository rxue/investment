package io.github.rxue.investment.portfolio.transactions;

public record Trade(String tickerSymbol, int shareAmount, Type type, long cents) implements Transaction {
    public enum Type {
        BUY, SELL
    }
}
