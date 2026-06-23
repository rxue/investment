package io.github.rxue.investment.portfolio.xirr.jpaentity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.rxue.investment.portfolio.holdings.jpaentity.Company;
import jakarta.persistence.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XIRRPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Company company;
    private int shareAmount;
    private Long euroCentMarketValue;
    @ManyToOne
    private XIRRRawInput rawInput;
    public XIRRPosition() {}
    public XIRRPosition(Company company, int shareAmount, XIRRRawInput rawInput) {
        this.company = company;
        this.shareAmount = shareAmount;
        this.rawInput = rawInput;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    public Long getEuroCentMarketValue() {
        return euroCentMarketValue;
    }

    public void setEuroCentMarketValue(Long euroCentMarketValue) {
        this.euroCentMarketValue = euroCentMarketValue;
    }
}
