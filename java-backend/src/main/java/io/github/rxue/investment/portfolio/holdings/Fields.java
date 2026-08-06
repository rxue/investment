package io.github.rxue.investment.portfolio.holdings;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public record Fields(List<OptionalField> optionalFields, Field sortBy) {
    private static final Set<Field> POST_CALCULATED_FIELDS = Set.of(OptionalField.PORTFOLIO_WEIGHT);
    List<OptionalField> commonOptionalFields() {
        return optionalFields.stream()
                .filter(((Predicate<OptionalField>) POST_CALCULATED_FIELDS::contains).negate())
                .toList();
    }
    List<OptionalField> getPostCalculatedFields() {
        return optionalFields.stream()
                .filter(((Predicate<OptionalField>) POST_CALCULATED_FIELDS::contains))
                .toList();
    }
}
