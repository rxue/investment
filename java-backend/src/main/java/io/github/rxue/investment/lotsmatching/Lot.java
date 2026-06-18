package io.github.rxue.investment.lotsmatching;

import io.github.rxue.investment.OPTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Lot {

    LocalDate date();

    int shareAmount();

    long valueInCent();

    record Buy(LocalDate date, int shareAmount, long valueInCent) implements Lot {
    }
    record Sell(LocalDate date, int shareAmount, long valueInCent) implements Lot {
    }
    static Map<String,List<Lot>> toLotsByCompanySymbol(List<OPTransaction> tradingTransactions) {
        final Pattern pattern = Pattern.compile("^\\s*([OM]):(.+?)\\s*/(\\d+)");
        Map<String, List<Lot>> result = new HashMap<>();
        for (OPTransaction t : tradingTransactions) {
            Matcher matcher = pattern.matcher(t.message());
            if (matcher.find()) {
                String action = matcher.group(1);
                String ticker = matcher.group(2).strip();
                int shareAmount = Integer.parseInt(matcher.group(3));
                long valueInCent = Math.round(Math.abs(t.amountInEuro()) * 100);
                Lot lot = "O".equals(action)
                        ? new Buy(t.effectiveDate(), shareAmount, valueInCent)
                        : new Sell(t.effectiveDate(), shareAmount, valueInCent);
                result.computeIfAbsent(ticker, k -> new ArrayList<>()).add(lot);
            }
        }
        return result;
    }
}
