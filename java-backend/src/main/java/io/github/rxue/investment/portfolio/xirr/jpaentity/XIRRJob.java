package io.github.rxue.investment.portfolio.xirr.jpaentity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class XIRRJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private XIRRJobStep step;
    private BigDecimal result;

    public Long getId() {
        return id;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }
}
