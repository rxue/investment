package io.github.rxue.investment.rest.infrastructure.persistence;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watch_list")
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "security_ids", nullable = false)
    private List<String> securityIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private List<QuoteMetric> quoteMetrics;

    protected WatchList() {
        // for JPA
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSecurityIds() {
        return securityIds;
    }

    public void setSecurityIds(List<String> securityIds) {
        // keep the Hibernate-managed collection instance, replace its contents
        this.securityIds.clear();
        this.securityIds.addAll(securityIds);
    }

    public List<QuoteMetric> getQuoteMetrics() {
        return quoteMetrics;
    }

    public void setQuoteMetrics(List<QuoteMetric> quoteMetrics) {
        this.quoteMetrics = quoteMetrics;
    }

}
