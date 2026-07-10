package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.transaction.Action;
import io.github.rxue.investment.portfolio.transaction.Trade;
import io.github.rxue.investment.portfolio.holdings.*;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OPHoldingsGenerator {
    private final OPTransactionExtractor opTransactionExtractor;
    private final YahooCompanySymbolRepository companySymbolRepository;
    private final HoldingsGenerator holdingsGenerator;

    private OPHoldingsGenerator(OPTransactionExtractor opTransactionExtractor,
                                YahooCompanySymbolRepository companySymbolRepository,
                                HoldingsGenerator holdingsGenerator) {
        this.opTransactionExtractor = opTransactionExtractor;
        this.companySymbolRepository = companySymbolRepository;
        this.holdingsGenerator = holdingsGenerator;
    }
    public OPHoldingsGenerator() {
        this(new OPTransactionExtractor(), new YahooCompanySymbolRepository(), new HoldingsGenerator());
    }

    private static final Pattern ACTION_PATTERN = Pattern.compile("^\\s*([OM]):(.+?)\\s*/(\\d+)");

    public List<Holding> generate(List<InputStream> csvPaths, Set<String> fieldNames) {
        List<Trade> trades = opTransactionExtractor.extract(csvPaths).stream()
                .map(this::toTrade)
                .filter(Objects::nonNull)
                .toList();
        List<Field> fields = fieldNames.stream().map(Field::valueOf).toList();
        return holdingsGenerator.generate(trades, fields.toArray(Field[]::new));
    }
    private Trade toTrade(OPTransaction tr) {
        Matcher matcher = ACTION_PATTERN.matcher(tr.message());
        if (!matcher.find()) {
            return null;
        }
        String action = matcher.group(1);
        String yahooCompanySymbol = companySymbolRepository.findBy(matcher.group(2).strip())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find the Yahoo company symbol with the given OP's defined company identifier " + matcher.group(2).strip()));
        int shareAmount = Integer.parseInt(matcher.group(3));
        return new Trade(tr.effectiveDate(),
                tr.amountInEuro(),
                yahooCompanySymbol,
                "O".equals(action) ? Action.BUY : Action.SELL,
                shareAmount);
    }
}