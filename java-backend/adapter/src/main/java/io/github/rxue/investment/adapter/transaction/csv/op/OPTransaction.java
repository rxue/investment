package io.github.rxue.investment.adapter.transaction.csv.op;

import io.github.rxue.investment.portfolio.transactions.Trade;
import io.github.rxue.investment.portfolio.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static io.github.rxue.investment.portfolio.transactions.Trade.Type.*;


record OPTransaction(String bookingDate, String amountInEuro, String category, String explanation, String message) {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");
    private static final Pattern TRADE_PATTERN = Pattern.compile("^\\s*([OM]):(.+?)\\s*/(\\d+)");

    Transaction toTransaction(QualifiedTickerRepository qualifiedTickerRepository) {
        BigDecimal amountInEuro = new BigDecimal(amountInEuro().replace(",","."));
        LocalDate date = LocalDate.parse(bookingDate, DATE_FORMAT);
        long euroCents = amountInEuro
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        if ("700".equals(category) && Set.of("PANO","NOSTO").contains(explanation)) {
            return toTrade(qualifiedTickerRepository, date, euroCents);
        }

        return new Transaction() {
            @Override
            public LocalDate date() {
                return date;
            }

            @Override
            public long cents() {
                return euroCents;
            }
        };
    }
    private Trade toTrade(QualifiedTickerRepository qualifiedTickerRepository, LocalDate date, long cents) {
        Matcher m = TRADE_PATTERN.matcher(message);
        if (m.lookingAt()) {                     // like Python's re.match
            String action   = m.group(1);          // "O" (osto = buy) or "M" (myynti = sell)
            String opSecurityId = m.group(2);          // security name, trailing spaces excluded
            int shareAmount = Integer.parseInt(m.group(3));
            return new Trade(
                    qualifiedTickerRepository.findQualifiedTicker(opSecurityId),
                    date,
                    shareAmount,
                    "O".equals(action) ? BUY : SELL,
                    cents
                    );
        }
        throw new IllegalStateException();
    }

}
