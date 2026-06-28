package io.github.rxue.investment.portfolio.xirr;

import java.math.BigDecimal;
import java.util.List;

public record XIRRResult(List<CashFlowInput> cashFlowList, BigDecimal value) {
}
