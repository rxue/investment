package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.transaction.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OPTransaction {
    private static YahooCompanySymbolRepository companySymbolRepository = new YahooCompanySymbolRepository();

    private final LocalDate effectiveDate;
    private final BigDecimal amountInEuro;
    private final int category;
    private final String explanation;
    private final String counterpart;
    private final String message;

    private OPTransaction(Builder builder) {
        this.effectiveDate = builder.effectiveDate;
        this.amountInEuro = builder.amountInEuro;
        this.category = builder.category;
        this.explanation = builder.explanation;
        this.counterpart = builder.counterpart;
        this.message = builder.message;
    }

    LocalDate effectiveDate() {
        return effectiveDate;
    }

    BigDecimal amountInEuro() {
        return amountInEuro;
    }

    String message() {
        return message;
    }

    /**
     * Convert OPTransaction to Transaction, which is used as input to XIRR calculation
     *
     * cases:
     * - Trade
     * - Desposit
     * - Expense - all other negative outflow
     * - other i.e. any kinda cash flow
     * @return
     */
    Transaction toTransaction() {
        final Pattern actionPattern = Pattern.compile("^\\s*([OM]):(.+?)\\s*/(\\d+)");
        Matcher matcher = actionPattern.matcher(message);
        if (matcher.find()) {
            String yahooSymbol = companySymbolRepository.findBy(matcher.group(2).strip())
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find Yahoo symbol for: " + matcher.group(2).strip()));
            Action action = "O".equals(matcher.group(1)) ? Action.BUY : Action.SELL;
            int shareAmount = Integer.parseInt(matcher.group(3));
            return new Trade(effectiveDate, amountInEuro, yahooSymbol, action, shareAmount);
        }
        if (category == 710 && "TILISIIRTO".equals(explanation)) {
            return new Deposit(effectiveDate, amountInEuro);
        }
        if (amountInEuro.signum() < 0) {
            return new Expense(effectiveDate, amountInEuro);
        }
        return new Transaction() {
            @Override
            public LocalDate date() {
                return effectiveDate;
            }

            @Override
            public BigDecimal moneyAmount() {
                return amountInEuro;
            }
        };
    }

    static class Builder {
        private LocalDate effectiveDate;
        private BigDecimal amountInEuro;
        private int category;
        private String explanation;
        private String counterpart;
        private String message;

        Builder effectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        Builder amountInEuro(BigDecimal amountInEuro) {
            this.amountInEuro = amountInEuro;
            return this;
        }

        Builder category(int category) {
            this.category = category;
            return this;
        }

        Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        Builder counterpart(String counterpart) {
            this.counterpart = counterpart;
            return this;
        }

        Builder message(String message) {
            this.message = message;
            return this;
        }

        OPTransaction build() {
            return new OPTransaction(this);
        }
    }
}
