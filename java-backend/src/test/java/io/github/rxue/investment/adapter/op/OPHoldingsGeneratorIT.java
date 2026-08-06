package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.holdings.CompulsoryField;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.OptionalField;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OPHoldingsGeneratorIT {
    private final OPHoldingsGenerator out = new OPHoldingsGenerator();

    @Test
    void generate_pfe_and_stz_holdings_without_sorting() {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("op_single_sub_period_transactions.csv");
        assertNotNull(csvInputStream, "test resource op_single_sub_period_transactions.csv must be on the classpath");

        List<Holding> holdings = out.generate(List.of(csvInputStream), Set.of("REPORT_MARKET_VALUE"), null);

        Map<String,Integer> positionsByCompanyId = holdings.stream()
                .collect(Collectors.toMap(h -> h.<String>value(CompulsoryField.COMPANY_ID), h -> h.<Integer>value(CompulsoryField.POSITION)));
        assertEquals(Map.of("PFE", 30, "STZ", 4), positionsByCompanyId);

        for (Holding holding : holdings) {
            BigDecimal reportMarketValue = holding.value(OptionalField.REPORT_MARKET_VALUE);
            assertTrue(reportMarketValue.compareTo(BigDecimal.ZERO) > 0,
                    "report market value should be positive, but was " + reportMarketValue);
        }
    }
}
