package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.portfolio.transaction.Action;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.*;

public class TradeLotsMatcher {
    private final LotsMatcher lotsMatcher;
    public TradeLotsMatcher(LotsMatcher lotsMatcher) {
        this.lotsMatcher = lotsMatcher;
    }
    public List<TradeLotsMatchResult> matchAllInFifo(List<Trade> trades, Map<String,List<Lot.Buy>> existingUnrealizedLots) {
        Map<String,List<Lot>> lotsByCompanyIdentifier = toLotsBySecurity(trades);
        List<TradeLotsMatchResult> matchResults = new ArrayList<>();
        for (Map.Entry<String,List<Lot>> entry : lotsByCompanyIdentifier.entrySet()) {
            String securityId = entry.getKey();
            MatchResult matchResult = lotsMatcher.matchInFifo(entry.getValue(), existingUnrealizedLots.getOrDefault(securityId, List.of()));
            matchResults.add(new TradeLotsMatchResult(securityId, matchResult));
        }
        return Collections.unmodifiableList(matchResults);
    }

    private static Map<String,List<Lot>> toLotsBySecurity(List<Trade> trades) {
        Map<String,List<Lot>> result = new HashMap<>();
        for (Trade t : trades) {
            Lot lot = t.action() == Action.BUY
                    ? new Lot.Buy(t.date(), t.shareAmount(), t.valueInCent())
                    : new Lot.Sell(t.date(), t.shareAmount(), t.valueInCent());
            result.computeIfAbsent(t.companyIdentifier(), k -> new ArrayList<>()).add(lot);
        }
        return result;
    }
}
