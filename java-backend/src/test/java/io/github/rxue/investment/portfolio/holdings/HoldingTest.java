package io.github.rxue.investment.portfolio.holdings;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HoldingTest {

    @Test
    void two_holdings_built_with_the_same_fields_and_values_are_equal() {
        Holding holding1 = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "PFE")
                .set(CompulsoryField.POSITION, 30)
                .build();
        Holding holding2 = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "PFE")
                .set(CompulsoryField.POSITION, 30)
                .build();

        assertEquals(holding1, holding2);
        assertEquals(holding1.hashCode(), holding2.hashCode());
    }

    @Test
    void holdings_with_different_values_are_not_equal() {
        Holding holding1 = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "PFE")
                .set(CompulsoryField.POSITION, 30)
                .build();
        Holding holding2 = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "STZ")
                .set(CompulsoryField.POSITION, 4)
                .build();

        assertNotEquals(holding1, holding2);
    }

    @Test
    void calling_set_then_build_twice_on_the_same_builder_produces_two_different_holdings() {
        Holding.Builder builder = new Holding.Builder(List.of(OptionalField.MARKET_VALUE_IN_EURO))
                .set(CompulsoryField.COMPANY_ID, "PFE")
                .set(CompulsoryField.POSITION, 30);

        Holding holding1 = builder.set(OptionalField.MARKET_VALUE_IN_EURO, BigDecimal.valueOf(637.16))
                .build();
        Holding holding2 = builder.set(OptionalField.MARKET_VALUE_IN_EURO, BigDecimal.valueOf(634.06))
                .build();

        assertNotEquals(holding1, holding2);
    }

}
