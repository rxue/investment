package io.github.rxue.investment.api;

import io.github.rxue.investment.adapter.marketquote.yahoofinance.YahooFinanceRepository;
import io.github.rxue.investment.marketquote.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public Repository marketQuoteRepository() {
        return new YahooFinanceRepository("EUR");
    }
}
