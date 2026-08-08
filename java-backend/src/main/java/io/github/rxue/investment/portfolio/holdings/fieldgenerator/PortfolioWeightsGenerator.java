package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.vo.Percentage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PortfolioWeightsGenerator implements FieldValuesGenerator<Percentage> {
    private final Map<String, BigDecimal> existingMarketValuesInEuro;
    private final ReportMarketValuesGenerator reportMarketValuesGenerator;
    private Map<String, BigDecimal> marketValuesInEuro;
    public PortfolioWeightsGenerator(Map<String, BigDecimal> existingMarketValuesInEuro, ReportMarketValuesGenerator reportMarketValuesGenerator) {
        this.existingMarketValuesInEuro = existingMarketValuesInEuro;
        this.reportMarketValuesGenerator = reportMarketValuesGenerator;
        this.marketValuesInEuro = this.existingMarketValuesInEuro;
    }

    @Override
    public Map<String, Percentage> generateGeneric(List<String> companyIds) {
        marketValuesInEuro = existingMarketValuesInEuro.isEmpty() ? reportMarketValuesGenerator.generateGeneric(companyIds) : existingMarketValuesInEuro;
        BigDecimal totalMarketValueInEuro = marketValuesInEuro.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return companyIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        companyId -> new Percentage(marketValuesInEuro.get(companyId)
                                .divide(totalMarketValueInEuro, MathContext.DECIMAL64))));
    }

    public Map<String, BigDecimal> getMarketValuesInEuro() {
        return marketValuesInEuro;
    }
}
