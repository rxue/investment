package io.github.rxue.investment.portfolio.irr.jpaentity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.rxue.investment.portfolio.holdings.Company;
import jakarta.persistence.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Company company;
    private int shareAmount;
    private Long euroCentMarketValue;
    @ManyToOne
    private IRRJob irrJob;

    private Position() {}

    public Position(IRRJob irrJob) {
        this.irrJob = irrJob;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(int shareAmount) {
        this.shareAmount = shareAmount;
    }

    public Long getEuroCentMarketValue() {
        return euroCentMarketValue;
    }

    public void setEuroCentMarketValue(Long euroCentMarketValue) {
        this.euroCentMarketValue = euroCentMarketValue;
    }
}
