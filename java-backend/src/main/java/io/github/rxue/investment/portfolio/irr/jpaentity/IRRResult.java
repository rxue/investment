package io.github.rxue.investment.portfolio.irr.jpaentity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class IRRResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private IRRJob job;
    @OneToMany(mappedBy= "irrResult", cascade = CascadeType.ALL)
    private List<XIRRCashFlow> cashFlows;
    @Column(name = "irr_value")
    private double value;
    public void setJob(IRRJob job) {
        this.job = job;
    }

    public IRRJob getJob() {
        return job;
    }

    public List<XIRRCashFlow> getCashFlows() {
        return cashFlows;
    }
    public void setCashFlows(List<XIRRCashFlow> cashFlows) {
        this.cashFlows = cashFlows;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
