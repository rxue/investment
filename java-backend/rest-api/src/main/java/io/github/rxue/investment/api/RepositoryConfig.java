package io.github.rxue.investment.api;

import io.github.rxue.investmentutility.fx.FxRateFetcher;
import io.github.rxue.investmentutility.fx.ecb.ECBFxRateFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.yahoofinance.YahooFinanceQuoteFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;


@Configuration
public class RepositoryConfig {

    @Bean
    public QuoteFetcher marketQuoteRepository() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        FxRateFetcher fxRateFetcher = new ECBFxRateFetcher(httpClient);
        return new YahooFinanceQuoteFetcher(httpClient, "EUR");
    }
    @Bean
    public JobRepository jobRepository() {
        return new JobRepository(marketQuoteRepository());
    }
}
