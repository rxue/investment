package io.github.rxue.investment.portfolio.xirr.jpaentity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class XIRRRawInput {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private XIRRJob job;
    @OneToMany(mappedBy = "rawInput", cascade = CascadeType.ALL)
    private List<Position> holdings;
    private Long cashInEuroCent;
    @OneToMany(mappedBy = "rawInput", cascade = CascadeType.ALL)
    private List<CashFlow> cashFlows;


    public XIRRRawInput() {}

    public XIRRRawInput(XIRRJob job) {
        this.job = job;
    }

    public XIRRJob getJob() {
        return job;
    }

    public List<Position> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Position> holdings) {
        this.holdings = holdings;
    }

    public Long getCashInEuroCent() {
        return cashInEuroCent;
    }

    public void setCashInEuroCent(Long cashInEuroCent) {
        this.cashInEuroCent = cashInEuroCent;
    }

    public List<CashFlow> getCashFlows() {
        return cashFlows;
    }

    public void setCashFlows(List<CashFlow> cashFlows) {
        this.cashFlows = cashFlows;
    }
}
