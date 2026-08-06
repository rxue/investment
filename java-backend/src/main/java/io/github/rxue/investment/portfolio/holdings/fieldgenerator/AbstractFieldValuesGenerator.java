package io.github.rxue.investment.portfolio.holdings.fieldgenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractFieldValuesGenerator<T> implements FieldValuesGenerator<T> {
    @Override
    public final Map<String,T> generateGeneric(List<String> companyIds) {
        Map<String,T> result = new HashMap<>();
        for (String companyId : companyIds) {
            result.put(companyId, getSingleValue(companyId));
        }
        return result;
    }
    abstract T getSingleValue(String companyId);
}
