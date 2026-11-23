package io.github.rxue.investment.application;

import io.github.rxue.investment.CumulativeReturnCalculator;
import io.github.rxue.investment.interfaces.web.ReturnValue;
import io.github.rxue.investmentutility.v2.marketquote.DailyPriceSeries;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.util.DailyValue;
import io.github.rxue.investmentutility.v2.util.Result;
import io.github.rxue.investmentutility.v2.vo.InclusiveDateRange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CumulativeReturnService {
    private final CumulativeReturnCalculator calculator;
    private final PriceSeriesFetcher priceSeriesFetcher;
    public CumulativeReturnService(CumulativeReturnCalculator calculator, PriceSeriesFetcher priceSeriesFetcher) {
        this.calculator = calculator;
        this.priceSeriesFetcher = priceSeriesFetcher;
    }

    /**
     * @throws NotFoundException if the price series of the given security is not found
     * @throws PriceSeriesFetchException if fetching the price series fails for any other reason
     */
    public List<ReturnValue> calculate(String securityId, InclusiveDateRange dateRange) {
        Result<DailyPriceSeries<Double>> priceSeriesResult = priceSeriesFetcher.fetchAdjustedClosePrices(securityId, dateRange);
        return switch (priceSeriesResult) {
            case Result.Success<DailyPriceSeries<Double>>(DailyPriceSeries<Double> priceSeries) ->
                    calculator.calculate(priceSeries.dailyValues()).stream()
                            .map(dailyValue -> new ReturnValue(dailyValue.date(), dailyValue.value()))
                            .toList();
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
}
