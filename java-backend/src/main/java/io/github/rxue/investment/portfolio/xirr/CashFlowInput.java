package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.xirr.jpaentity.CashFlow;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;

public final class CashFlowInput {
    private final LocalDate date;
    private final CashFlowType type;
    private final long valueInCent;
    public CashFlowInput(LocalDate date, CashFlowType type, long valueInCent) {
        this.date = date;
        this.type = type;
        this.valueInCent = valueInCent;
    }

    public LocalDate getDate() {
        return date;
    }

    public CashFlowType getType() {
        return type;
    }

    public long getValueInCent() {
        return valueInCent;
    }

    static CashFlowInput toInput(CashFlow cashFlow) {
        return new CashFlowInput(cashFlow.getDate(), CashFlowType.DEPOSIT, cashFlow.getValueInCent());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("date", date)
                .append("type", type)
                .append("valueInCent", valueInCent)
                .toString();
    }
}
