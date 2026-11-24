package io.github.rxue.investment.application;

import io.github.rxue.investment.domain.benchmark.BenchmarkComparison;
import io.github.rxue.investment.domain.benchmark.DailyReturn;
import io.github.rxue.investment.domain.benchmark.Returns;
import io.github.rxue.investment.returns.ReturnCalculator;
import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.util.DailyValue;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BenchmarkingService {
    private final ReturnCalculator calculator;
    private final PriceSeriesFetcher priceSeriesFetcher;

    public BenchmarkingService(ReturnCalculator calculator, PriceSeriesFetcher priceSeriesFetcher) {
        this.calculator = calculator;
        this.priceSeriesFetcher = priceSeriesFetcher;
    }

    public BenchmarkComparison calculate(String benchmarkSecurityId, InclusiveDateRange dateRange, List<String> comparedSecurityIds) {
        Returns benchmarkReturns = calculateReturns(benchmarkSecurityId, dateRange);
        List<Returns> compared = new ArrayList<>();
        for (String comparedSecurityId : comparedSecurityIds) {
            compared.add(calculateReturns(comparedSecurityId, dateRange));
        }
        return new BenchmarkComparison(benchmarkReturns, Collections.unmodifiableList(new ArrayList<>(compared)));
    }

    /**
     * @throws NotFoundException         if the price series of the given security is not found
     * @throws PriceSeriesFetchException if fetching the price series fails for any other reason
     */
    private Returns calculateReturns(String securityId, InclusiveDateRange dateRange) {
        Result<DailyPriceSeries<Double>> priceSeriesResult = priceSeriesFetcher.fetchAdjustedClosePrices(securityId, dateRange);
        return switch (priceSeriesResult) {
            case Result.Success<DailyPriceSeries<Double>>(DailyPriceSeries<Double> priceSeries) -> {
                    List<DailyValue<Double>> rawDailyReturnValues = calculator.calculate(priceSeries.dailyValues());
                    yield toReturns(securityId, rawDailyReturnValues);
            }
            case Result.ResponseError<DailyPriceSeries<Double>>(int statusCode, String description) -> {
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    throw new NotFoundException("price series of security " + securityId + " not found: " + description);
                }
                throw new PriceSeriesFetchException("fetching price series of security " + securityId
                        + " failed with status " + statusCode + ": " + description);
            }
            case Result.TechnicalError<DailyPriceSeries<Double>>(String description, Throwable cause) ->
                    throw new PriceSeriesFetchException("fetching price series of security " + securityId
                            + " failed: " + description, cause);
        };
    }

    private static Returns toReturns(String id, List<DailyValue<Double>> valueSeries) {
        List<DailyReturn> dailyReturns = valueSeries.stream()
                .map(v -> new DailyReturn(v.date(), v.value()))
                .toList();
        return new Returns(id, dailyReturns);
    }
}