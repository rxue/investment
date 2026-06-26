package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.portfolio.OPTransaction;

import java.util.*;

public class HoldingsGenerator {
    private final LotsMatcher lotsMatcher;
    private final HoldingGenerator holdingGenerator;
    private HoldingsGenerator(LotsMatcher lotsMatcher, HoldingGenerator holdingGenerator) {
        this.lotsMatcher = lotsMatcher;
        this.holdingGenerator = holdingGenerator;
    }

    public HoldingsGenerator() {
        this(new LotsMatcher(), new HoldingGenerator());
    }

    public List<Holding> generate(List<Trade> trades, List<Field> optionalFields) {
        return matchAllInFifo(trades).entrySet().stream()
                .map(entry -> holdingGenerator.generate(entry.getKey(), entry.getValue(), optionalFields))
                .toList();
    }
    public static Map<String,List<Lot>> getTradingLotsByCompanySymbol(List<OPTransaction> transactions) {
        throw new UnsupportedOperationException();
    }
    public static Map<String,MatchResult> matchLots(LotsMatcher lotsMatcher, Map<String,List<Lot>> lotsByCompanyIdentifier) {
        throw new UnsupportedOperationException();
    }

    private Map<String, MatchResult> matchAllInFifo(List<Trade> trades) {
        Map<String,List<Lot>> lotsByCompanyIdentifier = toLotsByCompanyIdentifier(trades);
        Map<String,MatchResult> matchResults = new HashMap<>();
        for (Map.Entry<String,List<Lot>> entry : lotsByCompanyIdentifier.entrySet()) {
            matchResults.put(entry.getKey(), lotsMatcher.matchInFifo(entry.getValue(), List.of()));
        }
        return Collections.unmodifiableMap(matchResults);
    }

    private static Map<String,List<Lot>> toLotsByCompanyIdentifier(List<Trade> trades) {
        Map<String,List<Lot>> result = new HashMap<>();
        for (Trade t : trades) {
            Lot lot = t.type() == Trade.Type.BUY
                    ? new Lot.Buy(t.date(), t.shareAmount(), t.valueInCent())
                    : new Lot.Sell(t.date(), t.shareAmount(), t.valueInCent());
            result.computeIfAbsent(t.companyIdentifier(), k -> new ArrayList<>()).add(lot);
        }
        return result;
    }
}
