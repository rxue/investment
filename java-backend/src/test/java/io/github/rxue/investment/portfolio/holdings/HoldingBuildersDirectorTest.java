package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.marketquote.MarketQuoteFetcher;
import io.github.rxue.investment.vo.Percentage;
import io.github.rxue.investment.vo.Price;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HoldingBuildersDirectorTest {

    @Mock
    private MarketQuoteFetcher marketQuoteFetcher;

    @Test
    void portfolio_weight_is_calculated_using_the_already_calculated_report_market_value() {
        final String pfe = "PFE";
        Map<String, List<Lot.Buy>> unrealizedLots = Map.of(
                pfe, List.of(new Lot.Buy(LocalDate.of(2025, 1, 2), 30, 500000L)));

        when(marketQuoteFetcher.getCurrentPriceInEuro(pfe))
                .thenReturn(new Price("EUR", BigDecimal.valueOf(637.16), null));

        List<Holding> holdings = new HoldingBuildersDirector(marketQuoteFetcher, null)
                .direct(unrealizedLots, List.of(OptionalField.REPORT_MARKET_VALUE, OptionalField.PORTFOLIO_WEIGHT));

        Holding pfeHolding = holdingFor(holdings, pfe);

        assertEquals(new Percentage(BigDecimal.ONE), pfeHolding.value(OptionalField.PORTFOLIO_WEIGHT));

        // PORTFOLIO_WEIGHT must reuse the market value already computed for REPORT_MARKET_VALUE
        // rather than fetching the price from the market quote fetcher a second time.
        verify(marketQuoteFetcher, times(1)).getCurrentPriceInEuro(pfe);
    }

    @Test
    void report_market_value_is_calculated_using_the_already_calculated_portfolio_weight() {
        final String pfe = "PFE";
        Map<String, List<Lot.Buy>> unrealizedLots = Map.of(
                pfe, List.of(new Lot.Buy(LocalDate.of(2025, 1, 2), 30, 500000L)));

        when(marketQuoteFetcher.getCurrentPriceInEuro(pfe))
                .thenReturn(new Price("EUR", BigDecimal.valueOf(637.16), null));

        List<Holding> holdings = new HoldingBuildersDirector(marketQuoteFetcher, null)
                .direct(unrealizedLots, List.of(OptionalField.PORTFOLIO_WEIGHT, OptionalField.REPORT_MARKET_VALUE));

        Holding pfeHolding = holdingFor(holdings, pfe);

        assertEquals(new Percentage(BigDecimal.ONE), pfeHolding.value(OptionalField.PORTFOLIO_WEIGHT));

        // PORTFOLIO_WEIGHT must reuse the market value already computed for REPORT_MARKET_VALUE
        // rather than fetching the price from the market quote fetcher a second time.
        verify(marketQuoteFetcher, times(1)).getCurrentPriceInEuro(pfe);
    }

    private static Holding holdingFor(List<Holding> holdings, String companyId) {
        return holdings.stream()
                .filter(holding -> companyId.equals(holding.<String>value(CompulsoryField.COMPANY_ID)))
                .findFirst()
                .orElseThrow();
    }
}
