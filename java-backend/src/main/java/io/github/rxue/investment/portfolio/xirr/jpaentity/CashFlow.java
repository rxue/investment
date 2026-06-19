package io.github.rxue.investment.portfolio.xirr.jpaentity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
public class CashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private XIRRRawInput rawInput;
    private CashFlowType type;
    private LocalDate date;
    private long valueInCent;


    public CashFlow() {}

    public CashFlow(XIRRRawInput rawInput, CashFlowType type, LocalDate date, long amountInCent) {
        this.rawInput = rawInput;
        this.type = type;
        this.date = date;
        this.valueInCent = amountInCent;
    }

    public CashFlowType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getValueInCent() {
        return valueInCent;
    }
}
