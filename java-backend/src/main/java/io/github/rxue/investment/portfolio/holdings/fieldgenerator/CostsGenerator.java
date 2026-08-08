package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.lotsmatching.Lot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CostsGenerator extends AbstractFieldValuesGenerator<BigDecimal> {
    private final Map<String,List<Lot.Buy>> unrealizedLots;
    public CostsGenerator(Map<String, List<Lot.Buy>> unrealizedLots) {
        this.unrealizedLots = unrealizedLots;
    }

    @Override
    BigDecimal getSingleValue(String companyId) {
        long costInCent = unrealizedLots.get(companyId)
                .stream()
                .mapToLong(Lot.Buy::valueInCent)
                .sum();
        return BigDecimal.valueOf(costInCent, 2);
    }
}
