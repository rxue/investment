package io.github.rxue.investment.portfolio.irr.jpaentity;

import io.github.rxue.investment.entity.Status;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class IRRJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Status status = Status.RUNNING;
    @OneToMany(mappedBy= "irrJob", cascade = CascadeType.ALL)
    private List<Position> holdings;

    @OneToMany(mappedBy= "irrJob", cascade = CascadeType.ALL)
    private List<CashFlow> cashFlows;

    private long cashInCent;
    private LocalDate assumedLiquidationDate;
    
    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Position> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Position> holdings) {
        this.holdings = holdings;
    }

    public List<CashFlow> getCashFlows() {
        return cashFlows;
    }

    public void setCashFlows(List<CashFlow> cashFlows) {
        this.cashFlows = cashFlows;
    }

    public long getCashInCent() {
        return cashInCent;
    }

    public void setCashInCent(long cashInCent) {
        this.cashInCent = cashInCent;
    }

    public LocalDate getAssumedLiquidationDate() {
        return assumedLiquidationDate;
    }

    public void setAssumedLiquidationDate(LocalDate assumedLiquidationDate) {
        this.assumedLiquidationDate = assumedLiquidationDate;
    }
}
