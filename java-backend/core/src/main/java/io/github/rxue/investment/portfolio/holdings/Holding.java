package io.github.rxue.investment.portfolio.holdings;


import io.github.rxue.investmentutility.lotsmatching.OpenLots;

import java.util.AbstractMap;
import java.util.Map;

public record Holding(String securityId, int position, long costInCent) {
    static Holding of(OpenLots openLots) {
        return new Holding(openLots.securityId(), openLots.shareAmount(), openLots.costInCent());
    }
    Map.Entry<String,Holding> toMapEntry() {
        return new AbstractMap.SimpleImmutableEntry<>(securityId, this);
    }

}
