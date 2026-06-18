package io.github.rxue.investment.portfolio.irr.jpaentity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
public class CashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate date;
    private long amountInCent;

    @ManyToOne
    private IRRJob irrJob;

    public CashFlow() {}

    public CashFlow(LocalDate date, long amountInCent, IRRJob irrJob) {
        this.date = date;
        this.amountInCent = amountInCent;
        this.irrJob = irrJob;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getAmountInCent() {
        return amountInCent;
    }
}
