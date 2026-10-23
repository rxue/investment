package io.github.rxue.investment.adapter.transaction.csv.op;



import io.github.rxue.investmentutility.vo.bankaccount.transaction.*;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Deposit;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.BUY;
import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.SELL;



record OPTransaction(String bookingDate, String amountInEuro, String category, String explanation, String message) {
    private static final Pattern TRADE_PATTERN = Pattern.compile("^\\s*([OM]):(.+?)\\s*/(\\d+)");
    public static final Pattern DIVIDEND_PAYMENT_PATTERN = Pattern.compile("([A-Z]{2}[A-Z0-9]{9}\\d)\\s+Osinkotuotto.*?Omistettu määrä\\s+(\\d+)\\s*Kpl");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");

    Transaction toTransaction(QualifiedTickerRepository qualifiedTickerRepository) {
        LocalDate date = LocalDate.parse(bookingDate, DATE_FORMAT);
        long euroCents = new BigDecimal(this.amountInEuro().replace(",","."))
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        if ("700".equals(category) && Set.of("PANO","NOSTO").contains(explanation)) {
            return toTrade(qualifiedTickerRepository, date, euroCents);
        } else if ("710".equals(category)) {
            if ("ARVOPAPERIT".equals(explanation))
                return toDividend(qualifiedTickerRepository, date, euroCents);
            else if ("TILISIIRTO".equals(explanation))
                return new Deposit(date, euroCents);
        } else if ("730".equals(category)) {
            return new ServiceCharge(date, euroCents);
        } else if (("700".equals(category) || "706".equals(category)) && "TILISIIRTO".equals(explanation)) {
            return new ExternalPayment(date, euroCents);
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
                    date,
                    "O".equals(action) ? BUY : SELL,
                    qualifiedTickerRepository.findQualifiedTicker(opSecurityId),
                    shareAmount,
                    cents
                    );
        }
        throw new IllegalStateException();
    }
    private Dividend toDividend(QualifiedTickerRepository qualifiedTickerRepository, LocalDate date, long cents) {
        Matcher m = DIVIDEND_PAYMENT_PATTERN.matcher(message);
        if (m.find()) {
            String isin = m.group(1);
            int shareAmount = Integer.parseInt(m.group(2));    // "Omistettu määrä" = amount of shares held
            return new Dividend(
                    date,
                    qualifiedTickerRepository.findQualifiedTickerByIsin(isin),
                    shareAmount,
                    cents
                    );
        }
        throw new IllegalStateException("Not a dividend payment: " + message);
    }

}
