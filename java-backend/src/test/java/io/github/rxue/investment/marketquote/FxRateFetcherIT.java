package io.github.rxue.investment.marketquote;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FxRateFetcherIT {

    @Test
    public void getFxRateFromEuro_when_quote_currency_is_usd() {
        HttpClient httpClient = HttpClient.newHttpClient();
        FxRateFetcher fetcher = new FxRateFetcher(httpClient);
        Map.Entry<LocalDate, BigDecimal> result = fetcher.getFxRateFromEuro("USD", LocalDate.of(2026,9,12));
        assertEquals(LocalDate.of(2026,9,11), result.getKey());
        assertEquals(new BigDecimal("1.1592"), result.getValue());
    }
}
