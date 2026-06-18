package io.github.rxue.investment.portfolio.irr.jpaentity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
public class XIRRCashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean isAssumedLiquidation;
    private LocalDate date;
    private long valueInCent;

    @ManyToOne
    private IRRResult irrResult;

    public XIRRCashFlow() {}

    public XIRRCashFlow(boolean isAssumedLiquidation, LocalDate date, long amountInCent, IRRResult irrResult) {
        this.isAssumedLiquidation = isAssumedLiquidation;
        this.date = date;
        this.valueInCent = amountInCent;
        this.irrResult = irrResult;
    }

    public boolean isAssumedLiquidation() {
        return isAssumedLiquidation;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getValueInCent() {
        return valueInCent;
    }

}
