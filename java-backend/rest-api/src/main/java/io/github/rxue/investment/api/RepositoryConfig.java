package io.github.rxue.investment.api;

import io.github.rxue.investment.fx.FxRateFetcher;
import io.github.rxue.investment.fx.ecb.ECBFxRateFetcher;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.marketquote.yahoofinance.YahooFinanceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;


@Configuration
public class RepositoryConfig {

    @Bean
    public Repository marketQuoteRepository() {
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        FxRateFetcher fxRateFetcher = new ECBFxRateFetcher(httpClient);
        return new YahooFinanceRepository(fxRateFetcher, httpClient);
    }
    @Bean
    public JobRepository jobRepository() {
        return new JobRepository(marketQuoteRepository());
    }
}
