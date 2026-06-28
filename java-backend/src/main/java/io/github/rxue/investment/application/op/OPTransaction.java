package io.github.rxue.investment.application.op;

import io.github.rxue.investment.portfolio.transaction.Action;
import io.github.rxue.investment.portfolio.transaction.Deposit;
import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record OPTransaction(LocalDate effectiveDate,
                    BigDecimal amountInEuro,
                    int category,
                    String explanation,
                    String message) {
    private static YahooCompanySymbolRepository companySymbolRepository = new YahooCompanySymbolRepository();

    /**
     * Convert OPTransaction to Transaction, which is used as input to XIRR calculation
     *
     * cases:
     * - Desposit
     * - Trade
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
}
