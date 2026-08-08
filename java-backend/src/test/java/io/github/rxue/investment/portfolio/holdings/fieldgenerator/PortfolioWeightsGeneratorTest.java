package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import io.github.rxue.investment.vo.Percentage;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PortfolioWeightsGeneratorTest {

    @Mock
    private ReportMarketValuesGenerator reportMarketValuesGenerator;

    @Test
    void portfolio_weight_is_still_calculated_when_no_market_values_are_given() {
        final String pfe = "PFE";
        List<String> companyIds = List.of(pfe);

        when(reportMarketValuesGenerator.generateGeneric(companyIds))
                .thenReturn(Map.of(pfe, BigDecimal.valueOf(19114.80)));

        Map<String, Percentage> weights = new PortfolioWeightsGenerator(Map.of(), reportMarketValuesGenerator)
                .generateGeneric(companyIds);

        assertEquals(new Percentage(BigDecimal.ONE), weights.get(pfe));
    }
}
