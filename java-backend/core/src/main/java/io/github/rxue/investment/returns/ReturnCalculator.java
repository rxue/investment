package io.github.rxue.investment.returns;


import io.github.rxue.investmentutility.v2.util.DailyValue;

import java.util.List;

public class ReturnCalculator {

    /**
     * Calculates the cumulative return of each day relative to the earliest day in the series,
     * i.e. {@code price(day) / price(firstDay) - 1}. The first day thus always has a return of 0.
     *
     * @return cumulative returns as fractions (0.05 means +5%), ordered by date ascending;
     *         an empty list if the series is empty
     * @throws IllegalArgumentException if the base (earliest) price is not a positive number
     */
    public List<DailyValue<Double>> calculate(List<DailyValue<Double>> adjustedClosePriceSeries) {
        if (adjustedClosePriceSeries.isEmpty()) {
            return List.of();
        }
        double basePrice = adjustedClosePriceSeries.getFirst().value();
        if (!(basePrice > 0)) {
            throw new IllegalArgumentException("Base price must be positive, but was " + basePrice);
        }
        return adjustedClosePriceSeries.stream()
                .map(dailyPrice -> new DailyValue<>(dailyPrice.date(), dailyPrice.value() / basePrice - 1))
                .toList();
    }
}
