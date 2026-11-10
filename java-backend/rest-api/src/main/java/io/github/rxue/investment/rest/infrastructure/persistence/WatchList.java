package io.github.rxue.investment.rest.infrastructure.persistence;

import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    private List<QuoteMetric> quoteMetrics;

    protected WatchList() {
        // for JPA
    }

    public WatchList(String name, List<String> securityIds) {
        this.name = name;
        this.securityIds.addAll(securityIds);
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
