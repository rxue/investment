package io.github.rxue.investment.infrastructure;

import io.github.rxue.investment.CumulativeReturnCalculator;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.yahoofinance.YahooFinanceQuoteFetcher;
import io.github.rxue.investmentutility.v2.marketquote.PriceSeriesFetcher;
import io.github.rxue.investmentutility.v2.marketquote.yahoofinance.chart.YahooPriceSeriesFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;


@Configuration
public class BeanConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public QuoteFetcher marketQuoteRepository(HttpClient httpClient) {
        return new YahooFinanceQuoteFetcher(httpClient, "EUR");
    }

    @Bean
    public PriceSeriesFetcher priceSeriesFetcher(HttpClient httpClient, ObjectMapper objectMapper) {
        return new YahooPriceSeriesFetcher(httpClient, objectMapper);
    }

    @Bean
    public CumulativeReturnCalculator cumulativeReturnCalculator() {
        return new CumulativeReturnCalculator();
    }
}
