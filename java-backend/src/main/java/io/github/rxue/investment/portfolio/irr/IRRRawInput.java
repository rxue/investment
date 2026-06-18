package io.github.rxue.investment.portfolio.irr;

import io.github.rxue.investment.portfolio.irr.jpaentity.CashFlow;
import io.github.rxue.investment.portfolio.irr.jpaentity.IRRResult;
import io.github.rxue.investment.portfolio.irr.jpaentity.Position;
import io.github.rxue.investment.portfolio.irr.jpaentity.XIRRCashFlow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record IRRRawInput(AssumedLiquidation assumedLiquidation, List<CashFlow> cashFlow) {
    public record AssumedLiquidation(LocalDate date, List<Position> holdings, long cashInEuroCent) {
        public XIRRCashFlow liquidation(IRRResult irrResult) {
            long stockMarketValueInCent = holdings.stream()
                    .mapToLong(Position::getEuroCentMarketValue)
                    .sum();
            return new XIRRCashFlow(true, date, stockMarketValueInCent + cashInEuroCent, irrResult);
        }
    }
    public List<XIRRCashFlow> toCashFlowInput(IRRResult irrResult) {
        List<XIRRCashFlow> realCashFlows = cashFlow.stream()
                .map(cf -> new XIRRCashFlow(false, cf.getDate(), cf.getAmountInCent(), irrResult))
                .toList();
        List<XIRRCashFlow> result = new ArrayList<>(realCashFlows);
        result.add(assumedLiquidation.liquidation(irrResult));
        return Collections.unmodifiableList(result);
    }
}
