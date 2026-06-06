package io.github.rxue.investment.lotsmatching;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.github.rxue.investment.lotsmatching.MatchResult.*;

@Service
public class LotsMatcher {
    private RealizedLotsGroup dequeue(Lot.Sell sellLot, Queue<Lot.Buy> remainingLots) {
        int remainingSellShareAmount = sellLot.shareAmount();
        List<Lot.Buy> matchedBuyLots = new ArrayList<>();
        while (remainingSellShareAmount > 0) {
            Lot.Buy dequeued = remainingLots.poll();
            int dequeucedSharedAmount = dequeued.shareAmount();
            if (remainingSellShareAmount >= dequeucedSharedAmount) {
                matchedBuyLots.add(dequeued);
                remainingSellShareAmount -= dequeucedSharedAmount;
            } else {
                long adjustedMatchedBuyValueInCent = dequeued.valueInCent() * remainingSellShareAmount / dequeucedSharedAmount;
                Lot.Buy adjustedBuy = new Lot.Buy(dequeued.date(), remainingSellShareAmount, adjustedMatchedBuyValueInCent);
                matchedBuyLots.add(adjustedBuy);
                int remainingBuyShareAmount = dequeucedSharedAmount - remainingSellShareAmount;
                Lot.Buy remainingBuy = new Lot.Buy(dequeued.date(), remainingBuyShareAmount, dequeued.valueInCent() * remainingBuyShareAmount / dequeucedSharedAmount);
                remainingLots.offer(remainingBuy);
                break;
            }
        }
        return new RealizedLotsGroup(sellLot, matchedBuyLots);
    }

    public MatchResult matchInFifo(List<Lot> lots, List<Lot.Buy> existingLots) {
        Queue<Lot.Buy> remainingLotQueue = new ArrayDeque<>(existingLots);
        List<RealizedLotsGroup> realizedLotsGroups = new ArrayList<>();
        for (Lot lot: lots) {
            if (lot instanceof Lot.Buy buyLot) {
                remainingLotQueue.offer(buyLot);
            } else if (lot instanceof Lot.Sell sellLot) {
                realizedLotsGroups.add(dequeue(sellLot, remainingLotQueue));
            }
        }
        return new MatchResult(new Unrealized(new ArrayList<>(remainingLotQueue)), new Realized(realizedLotsGroups));
    }
}
