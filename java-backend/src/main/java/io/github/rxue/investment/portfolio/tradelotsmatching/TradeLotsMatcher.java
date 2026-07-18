package io.github.rxue.investment.portfolio.tradelotsmatching;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.LotsMatcher;
import io.github.rxue.investment.lotsmatching.MatchResult;
import io.github.rxue.investment.portfolio.transaction.Action;
import io.github.rxue.investment.portfolio.transaction.Trade;

import java.util.*;

public class TradeLotsMatcher {
    private final LotsMatcher lotsMatcher;
    public TradeLotsMatcher() {
        this.lotsMatcher = new LotsMatcher();
    }
    public List<SingleSecurityTradeLotsMatchResult> matchAllInFifo(List<Trade> trades, Map<String,List<Lot.Buy>> existingUnrealizedLots) {
        Map<String,List<Lot>> lotsByCompanyIdentifier = toLotsBySecurity(trades);
        List<SingleSecurityTradeLotsMatchResult> matchResults = new ArrayList<>();
        for (Map.Entry<String,List<Lot>> entry : lotsByCompanyIdentifier.entrySet()) {
            String securityId = entry.getKey();
            MatchResult matchResult = lotsMatcher.matchInFifo(entry.getValue(), existingUnrealizedLots.getOrDefault(securityId, List.of()));
            matchResults.add(new SingleSecurityTradeLotsMatchResult(securityId, matchResult));
        }
        return Collections.unmodifiableList(matchResults);
    }
    public TradeLotsMatchResult matchInFifo(List<Trade> trades, Map<String,List<Lot.Buy>> existingUnrealizedLots) {
        Map<String,List<Lot>> lotsByCompanyIdentifier = toLotsBySecurity(trades);
        TradeLotsMatchResult.Builder tradeMatchResult = new TradeLotsMatchResult.Builder();
        for (Map.Entry<String,List<Lot>> entry : lotsByCompanyIdentifier.entrySet()) {
            String securityId = entry.getKey();
            MatchResult matchResult = lotsMatcher.matchInFifo(entry.getValue(), existingUnrealizedLots.getOrDefault(securityId, List.of()));
            tradeMatchResult.add(securityId, matchResult);
        }
        return tradeMatchResult.build();
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
