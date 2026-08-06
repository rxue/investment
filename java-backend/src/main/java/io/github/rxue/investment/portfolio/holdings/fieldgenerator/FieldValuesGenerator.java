package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FieldValuesGenerator<T> {
    Map<String,T> generateGeneric(List<String> companyIds);
    default Map<String,Object> generate(List<String> companyIds) {
        return toNonGeneric(generateGeneric(companyIds));
    }
    static <T> Map<String,Object> toNonGeneric(Map<String,T> values) {
        return Collections.unmodifiableMap(new HashMap<>(values));
    }
}
