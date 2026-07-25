package io.github.rxue.investment.portfolio.twr;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.PriceFetcher;
import io.github.rxue.investment.portfolio.Calculator;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatchResult;
import io.github.rxue.investment.portfolio.tradelotsmatching.TradeLotsMatcher;
import io.github.rxue.investment.portfolio.transaction.Deposit;
import io.github.rxue.investment.portfolio.transaction.Transaction;
import io.github.rxue.investment.portfolio.twr.output.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TWRCalculator implements Calculator<TWRResult> {

    @Override
    public TWRResult calculate(List<Transaction> transactions) {
        List<SubPeriod> subPeriods = new Splitter(transactions)
                .split();
        List<SubPeriodPortfolioSnapshots> subPeriodSnapshotsList = new SnapshotsGenerator()
                .apply(subPeriods);
        return new TWRResult(subPeriodSnapshotsList);
    }

    static class Splitter {
        private final List<Transaction> transactions;
        public Splitter(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        public List<SubPeriod> split() {
            List<SubPeriod> subPeriods = new ArrayList<>();
            while(true) {
                int startIndex = subPeriods.stream()
                        .mapToInt(SubPeriod::transactionCount)
                        .sum();
                if (startIndex >= transactions.size() - 1) break;
                SubPeriod subPeriod = getSubPeriod(startIndex);
                subPeriods.add(subPeriod);
            }
            return subPeriods;
        }

        private SubPeriod getSubPeriod(int startIndex) {
            int i = startIndex;
            List<Transaction> subPeriodTransactions = new ArrayList<>();
            LocalDate endDate = transactions.get(transactions.size()-1)
                    .date();
            while (i < transactions.size()) {
                subPeriodTransactions.add(transactions.get(i));
                if (i< transactions.size()-1 && transactions.get(i+1) instanceof Deposit deposit) {
                    endDate = deposit.date().minusDays(1);
                    break;
                }
                i++;
            }
            return new SubPeriod(subPeriodTransactions, endDate);
        }
    }

    static class SnapshotsGenerator implements Function<List<SubPeriod>,List<SubPeriodPortfolioSnapshots>> {
        private final TradeLotsMatcher tradeLotsMatcher = new TradeLotsMatcher();
        private final PriceFetcher priceFetcher = new PriceFetcher();

        @Override
        public List<SubPeriodPortfolioSnapshots> apply(List<SubPeriod> subPeriods) {
            List<SubPeriodPortfolioSnapshots> allSnapshots = new ArrayList<>();
            PortfolioRawSnapshot previousSubPeriodEndRawSnapshot = null;
            for (SubPeriod subPeriod : subPeriods) {
                BigDecimal firstExternalCashFlowAmount = subPeriod.transactions()
                        .getFirst()
                        .moneyAmount();
                PortfolioSnapshot subPeriodStartSnapshot;
                BigDecimal remainingCashInEuro;
                Map<String,List<Lot.Buy>> remainingUnrealizedLots;
                if (previousSubPeriodEndRawSnapshot == null) {
                    //first sub-period
                    remainingCashInEuro = firstExternalCashFlowAmount;
                    remainingUnrealizedLots = Map.of();

                } else {
                    remainingUnrealizedLots = previousSubPeriodEndRawSnapshot.getUnrealizedLots();
                    remainingCashInEuro = previousSubPeriodEndRawSnapshot.getCashInEuro()
                            .add(firstExternalCashFlowAmount);
                }
                subPeriodStartSnapshot = new PortfolioRawSnapshot(subPeriod.startDate(), remainingCashInEuro, remainingUnrealizedLots, priceFetcher)
                        .toSnapshot();
                //end date portfolio snapshot calculation
                remainingCashInEuro = remainingCashInEuro.add(subPeriod.summedCashInEuro().subtract(firstExternalCashFlowAmount));
                TradeLotsMatchResult matchResult = tradeLotsMatcher.matchInFifo(subPeriod.trades(), remainingUnrealizedLots);
                remainingUnrealizedLots = matchResult.unrealizedLotsMap();
                PortfolioRawSnapshot subPeriodEndRawSnapshot = new PortfolioRawSnapshot(subPeriod.endDate(), remainingCashInEuro, remainingUnrealizedLots, priceFetcher);
                allSnapshots.add(new SubPeriodPortfolioSnapshots(subPeriodStartSnapshot, subPeriodEndRawSnapshot.toSnapshot()));
                previousSubPeriodEndRawSnapshot = subPeriodEndRawSnapshot;
            }
            return allSnapshots;
        }
    }

}