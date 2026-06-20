package io.github.rxue.investment.portfolio.xirr.jpaentity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class CashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private XIRRRawInput rawInput;
    private LocalDate date;
    private long valueInCent;


    public CashFlow() {}

    public CashFlow(XIRRRawInput rawInput, LocalDate date, long amountInCent) {
        this.rawInput = rawInput;
        this.date = date;
        this.valueInCent = amountInCent;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getValueInCent() {
        return valueInCent;
    }

}
